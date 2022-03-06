package com.kereq.main.listener;

import com.kereq.common.constant.QueueName;
import com.kereq.communicator.shared.dto.ConnectionEventDTO;
import com.kereq.main.repository.UserRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ConnectionListener {

    private final UserRepository userRepository;

    public ConnectionListener(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @RabbitListener(queues = QueueName.CONNECTIONS)
    @Transactional
    public void onMessage(@Payload ConnectionEventDTO connectionEvent) {
        boolean newOnline = ConnectionEventDTO.Type.CONNECT.equals(connectionEvent.getType());
        userRepository.setOnlineByUserId(connectionEvent.getUserId(), newOnline);
    }
}
