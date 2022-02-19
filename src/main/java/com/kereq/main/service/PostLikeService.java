package com.kereq.main.service;

import com.kereq.common.error.RepositoryError;
import com.kereq.main.constant.LikeType;
import com.kereq.main.entity.PostLikeData;
import com.kereq.main.entity.PostStatisticsData;
import com.kereq.main.exception.ApplicationException;
import com.kereq.main.repository.PostLikeRepository;
import com.kereq.main.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostLikeService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostLikeRepository postLikeRepository;

    @Autowired
    private PostStatisticsService postStatisticsService;

    public PostLikeData getUserLike(long userId, long postId) {
        return postLikeRepository.findByUserIdAndPostId(userId, postId);
    }

    @Transactional
    public PostStatisticsData addLike(long userId, long postId) {
        PostLikeData postLike = postLikeRepository.findByUserIdAndPostId(userId, postId);
        if (postLike != null) {
            if (LikeType.LIKE == postLike.getType()) {
                throw new ApplicationException(RepositoryError.RESOURCE_ALREADY_EXISTS);
            }
            postLike.setType(LikeType.LIKE);
            postLikeRepository.save(postLike);
            return postStatisticsService.convertLike(postId, true);
        }
        postLike = createPostLikeData(userId, postId);
        postLike.setType(LikeType.LIKE);
        postLikeRepository.save(postLike);
        return postStatisticsService.addLike(postId);
    }

    @Transactional
    public PostStatisticsData removeLike(long userId, long postId) {
        PostLikeData postLike = postLikeRepository.findByUserIdAndPostId(userId, postId);
        if (postLike == null || LikeType.LIKE != postLike.getType()) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND);
        }
        postLikeRepository.delete(postLike);
        return postStatisticsService.removeLike(postId);
    }

    @Transactional
    public PostStatisticsData addDislike(long userId, long postId) {
        PostLikeData postLike = postLikeRepository.findByUserIdAndPostId(userId, postId);
        if (postLike != null) {
            if (LikeType.DISLIKE == postLike.getType()) {
                throw new ApplicationException(RepositoryError.RESOURCE_ALREADY_EXISTS);
            }
            postLike.setType(LikeType.DISLIKE);
            postLikeRepository.save(postLike);
            return postStatisticsService.convertLike(postId, false);
        }
        postLike = createPostLikeData(userId, postId);
        postLike.setType(LikeType.DISLIKE);
        postLikeRepository.save(postLike);
        return postStatisticsService.addDislike(postId);
    }

    @Transactional
    public PostStatisticsData removeDislike(long userId, long postId) {
        PostLikeData postLike = postLikeRepository.findByUserIdAndPostId(userId, postId);
        if (postLike == null || LikeType.DISLIKE != postLike.getType()) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND);
        }
        postLikeRepository.delete(postLike);
        return postStatisticsService.removeDislike(postId);
    }

    private PostLikeData createPostLikeData(long userId, long postId) {
        PostLikeData postLike = new PostLikeData();
        postLike.setPostId(postId);
        postLike.setUserId(userId);
        return postLike;
    }
}
