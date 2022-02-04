package com.kereq.common.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessagingService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendMessageToQueue(String queueName, Object messageObject) {
        rabbitTemplate.convertAndSend(queueName, queueName, messageObject);
    }
}
