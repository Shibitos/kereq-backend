package com.kereq.main.repository;

import com.kereq.common.repository.BaseRepository;
import com.kereq.main.entity.FindFriendData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface FindFriendRepository extends BaseRepository<FindFriendData> {

    boolean existsByUserId(Long userId);

    FindFriendData findByUserId(Long userId);

    @Query(value = "SELECT ff FROM FindFriendData ff JOIN UserData u ON u.id = :userId" +
            " WHERE ff.user.id <> :userId" +
            " AND ff.user.id NOT IN (SELECT fs.friendId FROM FriendshipData fs WHERE fs.userId = :userId)" +
            " AND (ff.minAge IS NULL OR ff.minAge <= :age)" +
            " AND (ff.maxAge IS NULL OR ff.maxAge >= :age)" +
            " AND (ff.gender IS NULL OR ff.gender = u.gender)")
    Page<FindFriendData> findAdsForUserId(Long userId, int age, Pageable page); //TODO: not in friends
}
