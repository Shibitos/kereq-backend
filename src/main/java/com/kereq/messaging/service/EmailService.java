package com.kereq.messaging.service;

import com.kereq.common.constant.Queue;
import com.kereq.common.error.CommonError;
import com.kereq.common.error.RepositoryError;
import com.kereq.common.service.MessagingService;
import com.kereq.main.exception.ApplicationException;
import com.kereq.messaging.entity.MessageData;
import com.kereq.messaging.entity.MessageTemplateData;
import com.kereq.messaging.repository.MessageRepository;
import com.sanctionco.jmail.JMail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
@Service
public class EmailService {

    private static final Pattern PARAM_PATTERN = Pattern.compile("\\{\\{.*?}}");

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private Environment env;

    @Autowired
    private MessagingService messagingService;

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
        messagingService.sendMessageToQueue(Queue.Constant.MESSAGES, message.getId());
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
