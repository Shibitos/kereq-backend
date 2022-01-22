package com.kereq.messaging.service;

import com.kereq.common.error.CommonError;
import com.kereq.common.error.RepositoryError;
import com.kereq.main.exception.ApplicationException;
import com.kereq.messaging.entity.MessageData;
import com.kereq.messaging.entity.MessageTemplateData;
import com.kereq.messaging.error.MessageError;
import com.kereq.messaging.repository.MessageRepository;
import com.sanctionco.jmail.JMail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
@Service
public class EmailService {

    public static final Integer MAX_RETRY_COUNT = 3; //TODO: params

    private static final Pattern PARAM_PATTERN = Pattern.compile("\\{\\{.*?}}");

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private Environment env;

    public MessageData createMessageFromTemplate(MessageTemplateData template, String to, Map<String, String> data) {
        if (ObjectUtils.isEmpty(env.getProperty("email.support"))) { //TODO: better, params
            throw new ApplicationException(CommonError.INVALID_ERROR, "sender");
        }
        if (!JMail.isValid(to)) {
            throw new ApplicationException(CommonError.INVALID_ERROR, "email");
        }
        MessageData message = new MessageData();
        message.setFrom(env.getProperty("email.support"));
        message.setTo(to);
        message.setSubject(appendParameters(template.getSubject(), data));
        message.setBody(appendParameters(template.getBody(), data)); //TODO: sanitizing
        message.setTemplate(template);
        message.setStatus(MessageData.Status.PENDING);
        message.setRetryCount(0);

        return messageRepository.save(message);
    }

    public void sendMessage(MessageData message) { //TODO: bulk message?
        if (!messageRepository.existsById(message.getId())) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND);
        }
        if (!MessageData.Status.PENDING.equals(message.getStatus())) {
            throw new ApplicationException(CommonError.INVALID_ERROR, "status");
        }

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(message.getFrom());
        mail.setTo(message.getTo());
        mail.setSubject(message.getSubject());
        mail.setText(message.getBody());

        try {
            mailSender.send(mail);
        } catch (MailException e) {
            message.setRetryCount(message.getRetryCount() + 1);
            if (message.getRetryCount() >= MAX_RETRY_COUNT) {
                message.setStatus(MessageData.Status.FAILED);
            }
            messageRepository.save(message);
            log.error("Error while sending message.", e);
            throw new ApplicationException(MessageError.UNABLE_TO_SEND);
        }
        message.setStatus(MessageData.Status.SENT);
        messageRepository.save(message);
    }

    private String appendParameters(String content, Map<String, String> params) {
        return PARAM_PATTERN.matcher(content).replaceAll(match -> {
            String key = match.group();
            key = key.substring(2, key.length() - 2);
            if (ObjectUtils.isEmpty(params) || !params.containsKey(key)) {
                throw new ApplicationException(CommonError.MISSING_ERROR, "parameter " + key);
            }
            return params.get(key);
        });
    }
}
