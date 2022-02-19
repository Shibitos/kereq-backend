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

    public void fillUserLikeType(long userId, PostStatisticsData postStatistics) {
        if (postStatistics == null) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND);
        }
        PostLikeData postLikeData = postLikeRepository
                .findByUserIdAndPostId(userId, postStatistics.getPostId());
        if (postLikeData != null) {
            postStatistics.setUserLikeType(postLikeData.getType());
        }
    }

    public PostStatisticsData initialize() {
        PostStatisticsData postStatistics = new PostStatisticsData();
        postStatistics.setLikesCount(0);
        postStatistics.setDislikesCount(0);
        postStatistics.setCommentsCount(0);
        return postStatistics;
    }

    @Transactional
    public PostStatisticsData convertLike(long postId, boolean toLike) {
        PostStatisticsData postStatistics = postStatisticsRepository.findByPostId(postId);
        if (postStatistics == null) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND);
        }
        postStatistics.setLikesCount(postStatistics.getLikesCount() + (toLike ? 1 : -1));
        postStatistics.setDislikesCount(postStatistics.getDislikesCount() + (toLike ? -1 : 1));
        return postStatistics;
    }

    @Transactional
    public PostStatisticsData addLike(long postId) {
        PostStatisticsData postStatistics = postStatisticsRepository.findByPostId(postId);
        if (postStatistics == null) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND);
        }
        postStatistics.setLikesCount(postStatistics.getLikesCount() + 1);
        return postStatistics;
    }

    @Transactional
    public PostStatisticsData removeLike(long postId) {
        PostStatisticsData postStatistics = postStatisticsRepository.findByPostId(postId);
        if (postStatistics == null) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND);
        }
        postStatistics.setLikesCount(postStatistics.getLikesCount() - 1);
        return postStatistics;
    }

    @Transactional
    public PostStatisticsData addDislike(long postId) {
        PostStatisticsData postStatistics = postStatisticsRepository.findByPostId(postId);
        if (postStatistics == null) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND);
        }
        postStatistics.setDislikesCount(postStatistics.getDislikesCount() + 1);
        return postStatistics;
    }

    @Transactional
    public PostStatisticsData removeDislike(long postId) {
        PostStatisticsData postStatistics = postStatisticsRepository.findByPostId(postId);
        if (postStatistics == null) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND);
        }
        postStatistics.setDislikesCount(postStatistics.getDislikesCount() - 1);
        return postStatistics;
    }

    @Transactional
    public PostStatisticsData addComment(long postId) {
        PostStatisticsData postStatistics = postStatisticsRepository.findByPostId(postId);
        if (postStatistics == null) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND);
        }
        postStatistics.setCommentsCount(postStatistics.getCommentsCount() + 1);
        return postStatistics;
    }

    @Transactional
    public PostStatisticsData removeComment(long postId) {
        PostStatisticsData postStatistics = postStatisticsRepository.findByPostId(postId);
        if (postStatistics == null) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND);
        }
        postStatistics.setCommentsCount(postStatistics.getCommentsCount() - 1);
        return postStatistics;
    }
}
