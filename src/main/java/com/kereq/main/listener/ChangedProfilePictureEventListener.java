package com.kereq.main.listener;

import com.kereq.common.util.DateUtil;
import com.kereq.communicator.shared.dto.NotificationDTO;
import com.kereq.main.event.ChangedProfilePictureEvent;
import com.kereq.main.repository.FriendshipRepository;
import com.kereq.messaging.sender.NotificationSender;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class ChangedProfilePictureEventListener {

    private final FriendshipRepository friendshipRepository;

    private final NotificationSender notificationSender;

    public ChangedProfilePictureEventListener(FriendshipRepository friendshipRepository, NotificationSender notificationSender) {
        this.friendshipRepository = friendshipRepository;
        this.notificationSender = notificationSender;
    }

    @EventListener
    public void handleEvent(ChangedProfilePictureEvent event) {
        UUID eventUUID = UUID.randomUUID();
        Date eventDate = DateUtil.now();
        NotificationDTO.NotificationDTOBuilder baseNotificationBuilder = NotificationDTO.builder()
                .uuid(eventUUID)
                .date(eventDate)
                .sourceUserId(event.getUserId());
        Page<Long> friendsId = friendshipRepository.findUserFriendsId(event.getUserId(), Pageable.unpaged());
        friendsId.forEach(friendId -> sendNotificationToUser(baseNotificationBuilder, friendId));
    }

    private void sendNotificationToUser(NotificationDTO.NotificationDTOBuilder notificationBuilder, Long recipientId) {
        notificationBuilder
                .recipientId(recipientId)
                .title("test")
                .content("test");
        notificationSender.send(notificationBuilder.build());
    }
}
