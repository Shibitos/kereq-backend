package com.kereq.main.repository;

import com.kereq.common.repository.BaseRepository;
import com.kereq.main.entity.PostData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends BaseRepository<PostData> {

    @Query(value = "SELECT p FROM PostData p " +
            " WHERE p.user.id = :userId OR p.user.id IN (" +
            " SELECT f.friendId FROM FriendshipData f WHERE f.userId = :userId " +
            " ) ")
    Page<PostData> findPostsForUser(Long userId, Pageable page);

    Page<PostData> findByUserId(Long userId, Pageable page);
}
