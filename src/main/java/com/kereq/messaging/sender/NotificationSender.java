package com.kereq.messaging.sender;

import com.kereq.common.constant.ExchangeName;
import com.kereq.communicator.shared.dto.NotificationDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationSender {

    private final RabbitTemplate rabbitTemplate;

    public NotificationSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void send(NotificationDTO notificationDTO) {
        rabbitTemplate.convertAndSend(ExchangeName.NOTIFICATIONS_NOTIFICATION, "", notificationDTO);
    }
}
