package com.kereq.main.repository;

import com.kereq.common.repository.BaseRepository;
import com.kereq.main.entity.FriendshipData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface FriendshipRepository extends BaseRepository<FriendshipData> {

    boolean existsByUserIdAndFriendId(Long userId, Long friendId);

    FriendshipData findByUserIdAndFriendId(Long userId, Long friendId);

    @Query("SELECT new FriendshipData(fs.friend, true, fs.auditMD) FROM FriendshipData fs WHERE fs.userId = :userId" +
            " AND fs.status = '" + FriendshipData.FriendshipStatus.ACCEPTED + "'")
    Page<FriendshipData> findUserFriends(Long userId, Pageable page);

    @Query("SELECT new FriendshipData(fs.user, false, fs.auditMD) FROM FriendshipData fs WHERE fs.friendId = :userId" +
            " AND fs.status = '" + FriendshipData.FriendshipStatus.INVITED + "'")
    Page<FriendshipData> findUserInvitations(Long userId, Pageable page);
}
