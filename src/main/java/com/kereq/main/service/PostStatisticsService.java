package com.kereq.main.service;

import com.kereq.common.error.RepositoryError;
import com.kereq.main.entity.PostLikeData;
import com.kereq.main.entity.PostStatisticsData;
import com.kereq.main.exception.ApplicationException;
import com.kereq.main.repository.PostLikeRepository;
import com.kereq.main.repository.PostRepository;
import com.kereq.main.repository.PostStatisticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostStatisticsService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostLikeRepository postLikeRepository;

    @Autowired
    private PostStatisticsRepository postStatisticsRepository;

    public PostStatisticsData getStatistics(Long userId, Long postId) {
        PostStatisticsData postStatistics = postStatisticsRepository.findByPostId(postId);
        if (postStatistics == null) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND);
        }
        PostLikeData postLikeData = postLikeRepository.findByUserIdAndPostId(userId, postId);
        if (postLikeData != null) {
            postStatistics.setUserLikeType(postLikeData.getType());
        }
        return postStatistics;
    }

    public PostStatisticsData initialize(Long postId) {
        if (postStatisticsRepository.existsByPostId(postId)) {
            throw new ApplicationException(RepositoryError.RESOURCE_ALREADY_EXISTS);
        }
        PostStatisticsData postStatistics = new PostStatisticsData();
        postStatistics.setPostId(postId);
        postStatistics.setLikesCount(0);
        postStatistics.setDislikesCount(0);
        postStatistics.setCommentsCount(0);
        return postStatisticsRepository.save(postStatistics);
    }

    public void remove(Long postId) {
        PostStatisticsData postStatistics = postStatisticsRepository.findByPostId(postId);
        if (postStatistics == null) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND);
        }
        postStatisticsRepository.delete(postStatistics);
    }

    @Transactional
    public PostStatisticsData convertLike(Long postId, boolean toLike) {
        PostStatisticsData postStatistics = postStatisticsRepository.findByPostId(postId);
        if (postStatistics == null) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND);
        }
        postStatistics.setLikesCount(postStatistics.getLikesCount() + (toLike ? 1 : -1));
        postStatistics.setDislikesCount(postStatistics.getDislikesCount() + (toLike ? -1 : 1));
        return postStatistics;
    }

    @Transactional
    public PostStatisticsData addLike(Long postId) {
        PostStatisticsData postStatistics = postStatisticsRepository.findByPostId(postId);
        if (postStatistics == null) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND);
        }
        postStatistics.setLikesCount(postStatistics.getLikesCount() + 1);
        return postStatistics;
    }

    @Transactional
    public PostStatisticsData removeLike(Long postId) {
        PostStatisticsData postStatistics = postStatisticsRepository.findByPostId(postId);
        if (postStatistics == null) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND);
        }
        postStatistics.setLikesCount(postStatistics.getLikesCount() - 1);
        return postStatistics;
    }

    @Transactional
    public PostStatisticsData addDislike(Long postId) {
        PostStatisticsData postStatistics = postStatisticsRepository.findByPostId(postId);
        if (postStatistics == null) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND);
        }
        postStatistics.setDislikesCount(postStatistics.getDislikesCount() + 1);
        return postStatistics;
    }

    @Transactional
    public PostStatisticsData removeDislike(Long postId) {
        PostStatisticsData postStatistics = postStatisticsRepository.findByPostId(postId);
        if (postStatistics == null) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND);
        }
        postStatistics.setDislikesCount(postStatistics.getDislikesCount() - 1);
        return postStatistics;
    }

    @Transactional
    public PostStatisticsData addComment(Long postId) {
        PostStatisticsData postStatistics = postStatisticsRepository.findByPostId(postId);
        if (postStatistics == null) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND);
        }
        postStatistics.setCommentsCount(postStatistics.getCommentsCount() + 1);
        return postStatistics;
    }

    @Transactional
    public PostStatisticsData removeComment(Long postId) {
        PostStatisticsData postStatistics = postStatisticsRepository.findByPostId(postId);
        if (postStatistics == null) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND);
        }
        postStatistics.setCommentsCount(postStatistics.getCommentsCount() - 1);
        return postStatistics;
    }
}
