package com.kereq.main.repository;

import com.kereq.common.repository.BaseRepository;
import com.kereq.main.entity.FriendshipData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface FriendshipRepository extends BaseRepository<FriendshipData> {

    boolean existsByUserIdAndFriendId(long userId, long friendId);

    FriendshipData findByUserIdAndFriendId(long userId, long friendId);

    @Query("SELECT new FriendshipData(fs.friend, true, fs.auditMD) FROM FriendshipData fs WHERE fs.userId = :userId" +
            " AND fs.status = '" + FriendshipData.FriendshipStatus.ACCEPTED + "'")
    Page<FriendshipData> findUserFriends(long userId, Pageable page);

    @Query("SELECT fs.friend.id FROM FriendshipData fs WHERE fs.userId = :userId" +
            " AND fs.status = '" + FriendshipData.FriendshipStatus.ACCEPTED + "' AND fs.friend.online IS TRUE")
    Page<Long> findUserFriendsIdOnline(long userId, Pageable page);

    @Query("SELECT new FriendshipData(fs.user, false, fs.auditMD) FROM FriendshipData fs WHERE fs.friendId = :userId" +
            " AND fs.status = '" + FriendshipData.FriendshipStatus.INVITED + "'")
    Page<FriendshipData> findUserInvitations(long userId, Pageable page);
}
