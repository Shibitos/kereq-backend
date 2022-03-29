package com.kereq.messaging.sender;

import com.kereq.common.constant.ExchangeName;
import com.kereq.communicator.shared.dto.ConnectionEventDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class ConnectionEventSender {

    private final RabbitTemplate rabbitTemplate;

    public ConnectionEventSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void send(ConnectionEventDTO connectionEvent) {
        rabbitTemplate.convertAndSend(ExchangeName.CONNECTIONS_WEBSOCKET, "", connectionEvent);
    }
}
