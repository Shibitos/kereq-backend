package com.kereq.messaging.listener;

import com.kereq.common.constant.QueueName;
import com.kereq.common.error.RepositoryError;
import com.kereq.main.exception.ApplicationException;
import com.kereq.messaging.entity.MessageData;
import com.kereq.messaging.error.MessageError;
import com.kereq.messaging.repository.MessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@Slf4j
@Component
public class MessageListener {

    public static final Integer MAX_RETRY_COUNT = 3; //TODO: params

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private EntityManager entityManager;

    @RabbitListener(queues = QueueName.MESSAGES)
    @Transactional
    public void onMessage(@Payload long messageId) {
        MessageData message = messageRepository.findForSendingById(messageId);
        if (message == null) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND);
        }
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(message.getFrom());
        mail.setTo(message.getTo());
        mail.setSubject(message.getSubject());
        mail.setText(message.getBody());
        try {
            mailSender.send(mail);
        } catch (Exception e) {
            message.setRetryCount(message.getRetryCount() + 1);
            if (message.getRetryCount() >= MAX_RETRY_COUNT) {
                message.setStatus(MessageData.Status.FAILED);
            }
            if (e.getMessage() != null) {
                message.setErrorMessage(StringUtils.abbreviate(e.getMessage(), 100));
            }
            messageRepository.save(message);
            log.error("Error while sending message (id: {}).", message.getId(), e);
            throw new ApplicationException(MessageError.UNABLE_TO_SEND);
        }
        message.setStatus(MessageData.Status.SENT);
    }
}
