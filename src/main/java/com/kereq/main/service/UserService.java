package com.kereq.main.service;

import com.kereq.common.error.CommonError;
import com.kereq.common.error.RepositoryError;
import com.kereq.main.entity.FriendshipData;
import com.kereq.main.entity.UserData;
import com.kereq.main.exception.ApplicationException;
import com.kereq.main.repository.FriendshipRepository;
import com.kereq.main.repository.PhotoRepository;
import com.kereq.main.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    
    private static final String STATUS_FIELD = "status"; //TODO: remove?
    
    private final UserRepository userRepository;

    private final PhotoRepository photoRepository;

    private final FriendshipRepository friendshipRepository;

    public UserService(UserRepository userRepository, PhotoRepository photoRepository, FriendshipRepository friendshipRepository) {
        this.userRepository = userRepository;
        this.photoRepository = photoRepository;
        this.friendshipRepository = friendshipRepository;
    }

    public UserData modifyUser(long userId, UserData user) {
        UserData original = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND));
        original.setFirstName(user.getFirstName());
        original.setLastName(user.getLastName());
        original.setCountry(user.getCountry());
        return userRepository.save(original);
    }

    public UserData modifyUserBiography(long userId, String biography) {
        UserData original = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND));
        original.setBiography(biography);
        return userRepository.save(original);
    }

    public void inviteFriend(long userId, long receiverId) {
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

    public void removeInvitation(long userId, long receiverId) {
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
    public void acceptInvitation(long userId, long senderId) {
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
    public void rejectInvitation(long userId, long senderId) {
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
    public void removeFriend(long userId, long friendId) {
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

    public Page<FriendshipData> getFriends(long userId, Pageable page) {
        return friendshipRepository.findUserFriends(userId, page);
    }

    public Page<FriendshipData> getInvitationsUsers(long userId, Pageable page) {
        return friendshipRepository.findUserInvitations(userId, page);
    }

    public FriendshipData getFriendship(long userId, long friendId) {
        return friendshipRepository.findByUserIdAndFriendId(userId, friendId);
    }

    public UserData getUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND));
    }
}
