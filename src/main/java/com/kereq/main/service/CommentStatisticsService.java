package com.kereq.main.service;

import com.kereq.common.error.RepositoryError;
import com.kereq.main.entity.CommentLikeData;
import com.kereq.main.entity.CommentStatisticsData;
import com.kereq.main.exception.ApplicationException;
import com.kereq.main.repository.CommentLikeRepository;
import com.kereq.main.repository.CommentStatisticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentStatisticsService {

    private final CommentLikeRepository commentLikeRepository;

    private final CommentStatisticsRepository commentStatisticsRepository;

    public CommentStatisticsService(CommentLikeRepository commentLikeRepository, CommentStatisticsRepository commentStatisticsRepository) {
        this.commentLikeRepository = commentLikeRepository;
        this.commentStatisticsRepository = commentStatisticsRepository;
    }

    public void fillUserLikeType(long userId, CommentStatisticsData commentStatistics) {
        if (commentStatistics == null) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND);
        }
        CommentLikeData commentLikeData = commentLikeRepository
                .findByUserIdAndCommentId(userId, commentStatistics.getCommentId());
        if (commentLikeData != null) {
            commentStatistics.setUserLikeType(commentLikeData.getType());
        }
    }

    public CommentStatisticsData initialize() {
        CommentStatisticsData commentStatistics = new CommentStatisticsData();
        commentStatistics.setLikesCount(0);
        commentStatistics.setDislikesCount(0);
        return commentStatistics;
    }

    @Transactional
    public CommentStatisticsData convertLike(long commentId, boolean toLike) {
        CommentStatisticsData commentStatistics = commentStatisticsRepository.findByCommentId(commentId);
        if (commentStatistics == null) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND);
        }
        commentStatistics.setLikesCount(commentStatistics.getLikesCount() + (toLike ? 1 : -1));
        commentStatistics.setDislikesCount(commentStatistics.getDislikesCount() + (toLike ? -1 : 1));
        return commentStatistics;
    }

    @Transactional
    public CommentStatisticsData addLike(long commentId) {
        CommentStatisticsData commentStatistics = commentStatisticsRepository.findByCommentId(commentId);
        if (commentStatistics == null) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND);
        }
        commentStatistics.setLikesCount(commentStatistics.getLikesCount() + 1);
        return commentStatistics;
    }

    @Transactional
    public CommentStatisticsData removeLike(long commentId) {
        CommentStatisticsData commentStatistics = commentStatisticsRepository.findByCommentId(commentId);
        if (commentStatistics == null) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND);
        }
        commentStatistics.setLikesCount(commentStatistics.getLikesCount() - 1);
        return commentStatistics;
    }

    @Transactional
    public CommentStatisticsData addDislike(long commentId) {
        CommentStatisticsData commentStatistics = commentStatisticsRepository.findByCommentId(commentId);
        if (commentStatistics == null) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND);
        }
        commentStatistics.setDislikesCount(commentStatistics.getDislikesCount() + 1);
        return commentStatistics;
    }

    @Transactional
    public CommentStatisticsData removeDislike(long commentId) {
        CommentStatisticsData commentStatistics = commentStatisticsRepository.findByCommentId(commentId);
        if (commentStatistics == null) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND);
        }
        commentStatistics.setDislikesCount(commentStatistics.getDislikesCount() - 1);
        return commentStatistics;
    }
}
