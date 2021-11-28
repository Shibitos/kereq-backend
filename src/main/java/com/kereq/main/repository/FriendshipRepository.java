package com.kereq.main.repository;

import com.kereq.main.entity.FriendshipData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendshipRepository extends JpaRepository<FriendshipData, Long> {

    boolean existsByUserIdAndFriendId(Long userId, Long friendId);

    FriendshipData findByUserIdAndFriendId(Long userId, Long friendId);
}
