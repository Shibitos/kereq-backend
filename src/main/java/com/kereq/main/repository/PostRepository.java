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

//    @Query(value = "SELECT DISTINCT p.* FROM POSTS p LEFT JOIN COMMENTS c ON c.COMM_ID IN (SELECT COMM_ID FROM COMMENTS cc WHERE cc.COMM_POST_ID = POST_ID LIMIT 3) " +
//            " WHERE POST_USER_ID = :userId OR POST_USER_ID IN (" +
//            " SELECT FRS_FRIEND_ID FROM FRIENDSHIPS WHERE FRS_USER_ID = :userId " +
//            " ) ",
//            countQuery = "SELECT COUNT(DISTINCT p.*) FROM POSTS p LEFT JOIN COMMENTS c ON c.COMM_ID IN (SELECT COMM_ID FROM COMMENTS cc WHERE cc.COMM_POST_ID = POST_ID LIMIT 3) " +
//                    " WHERE POST_USER_ID = :userId OR POST_USER_ID IN (" +
//                    " SELECT FRS_FRIEND_ID FROM FRIENDSHIPS WHERE FRS_USER_ID = :userId " +
//                    " )",
//            nativeQuery = true)
//    Page<PostData> findPostsForUser(Long userId, Pageable pageable);

    Page<PostData> findByUserId(Long userId, Pageable page);
}
