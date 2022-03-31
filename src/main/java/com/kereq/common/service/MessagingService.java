package com.kereq.common.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessagingService {

    private final RabbitTemplate rabbitTemplate;

    public MessagingService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMessageFanout(String exchange, Object messageObject) {
        rabbitTemplate.convertAndSend(exchange, "", messageObject);
    }
}
