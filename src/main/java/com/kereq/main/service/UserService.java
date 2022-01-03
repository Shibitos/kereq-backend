package com.kereq.main.service;

import com.kereq.common.error.CommonError;
import com.kereq.common.error.RepositoryError;
import com.kereq.main.entity.FriendshipData;
import com.kereq.main.entity.UserData;
import com.kereq.main.exception.ApplicationException;
import com.kereq.main.repository.FriendshipRepository;
import com.kereq.main.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendshipRepository friendshipRepository;

    private static final String STATUS_FIELD = "status"; //TODO: remove?

    public void inviteFriend(Long userId, Long receiverId) {
        if (friendshipRepository.existsByUserIdAndFriendId(userId, receiverId)
                || friendshipRepository.existsByUserIdAndFriendId(receiverId, userId)) {
            throw new ApplicationException(RepositoryError.RESOURCE_ALREADY_EXISTS);
        }
        FriendshipData friendship = new FriendshipData();
        friendship.setUserId(userId);
        friendship.setFriendId(receiverId);
        friendship.setStatus(FriendshipData.FriendshipStatus.INVITED);
        friendshipRepository.save(friendship);
    }

    public void removeInvitation(Long userId, Long receiverId) {
        FriendshipData invitation = friendshipRepository.findByUserIdAndFriendId(userId, receiverId);
        if (invitation == null) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND);
        }
        if (!FriendshipData.FriendshipStatus.INVITED.equals(invitation.getStatus())) {
            throw new ApplicationException(CommonError.INVALID_ERROR, STATUS_FIELD);
        }
        friendshipRepository.delete(invitation);
    }

    @Transactional
    public void acceptInvitation(Long userId, Long senderId) {
        FriendshipData invitation = friendshipRepository.findByUserIdAndFriendId(senderId, userId);
        if (invitation == null) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND);
        }
        if (!FriendshipData.FriendshipStatus.INVITED.equals(invitation.getStatus())) {
            throw new ApplicationException(CommonError.INVALID_ERROR, STATUS_FIELD);
        }
        invitation.setStatus(FriendshipData.FriendshipStatus.ACCEPTED);
        friendshipRepository.save(invitation);

        FriendshipData accepted = new FriendshipData();
        accepted.setUserId(userId);
        accepted.setFriendId(senderId);
        accepted.setStatus(FriendshipData.FriendshipStatus.ACCEPTED);
        friendshipRepository.save(accepted);
    }

    @Transactional
    public void rejectInvitation(Long userId, Long senderId) {
        FriendshipData invitation = friendshipRepository.findByUserIdAndFriendId(senderId, userId);
        if (invitation == null) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND);
        }
        if (!FriendshipData.FriendshipStatus.INVITED.equals(invitation.getStatus())) {
            throw new ApplicationException(CommonError.INVALID_ERROR, STATUS_FIELD);
        }
        invitation.setStatus(FriendshipData.FriendshipStatus.DECLINED);
        friendshipRepository.save(invitation);
    }

    @Transactional
    public void removeFriend(Long userId, Long friendId) {
        FriendshipData userEntry = friendshipRepository.findByUserIdAndFriendId(userId, friendId);
        if (userEntry == null) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND);
        }
        FriendshipData friendEntry = friendshipRepository.findByUserIdAndFriendId(friendId, userId);
        if (friendEntry == null) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND);
        }
        friendshipRepository.delete(userEntry);
        friendshipRepository.delete(friendEntry);
    }

    public Page<FriendshipData> getFriends(Long userId, Pageable page) {
        return friendshipRepository.findUserFriends(userId, page);
    }

    public Page<FriendshipData> getInvitationsUsers(Long userId, Pageable page) {
        return friendshipRepository.findUserInvitations(userId, page);
    }

    public UserData getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND));
    }
}
