package com.kereq.main.listener;

import com.kereq.common.constant.QueueName;
import com.kereq.communicator.shared.dto.ConnectionEventDTO;
import com.kereq.main.repository.FriendshipRepository;
import com.kereq.main.repository.UserRepository;
import com.kereq.main.sender.ConnectionEventSender;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ConnectionListener {

    private final UserRepository userRepository;

    private final FriendshipRepository friendshipRepository;

    private final ConnectionEventSender connectionEventSender;

    public ConnectionListener(UserRepository userRepository,
                              FriendshipRepository friendshipRepository,
                              ConnectionEventSender connectionEventSender) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
        this.connectionEventSender = connectionEventSender;
    }

    @RabbitListener(queues = QueueName.CONNECTIONS_BACKEND)
    @Transactional
    public void onMessage(@Payload ConnectionEventDTO connectionEvent) {
        boolean newOnline = ConnectionEventDTO.Type.CONNECT.equals(connectionEvent.getType());
        userRepository.setOnlineByUserId(connectionEvent.getUserId(), newOnline);
        Page<Long> friendsId = friendshipRepository.findUserFriendsIdOnline(
                connectionEvent.getUserId(), Pageable.unpaged()
        );
        friendsId.forEach(friendId -> {
            ConnectionEventDTO friendConnectionEvent = new ConnectionEventDTO(
                    connectionEvent.getType(), connectionEvent.getUserId(), friendId
            );
            connectionEventSender.send(friendConnectionEvent);
        });
    }
}
