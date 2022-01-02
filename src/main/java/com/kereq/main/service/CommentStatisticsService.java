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

    @Autowired
    private CommentLikeRepository commentLikeRepository;

    @Autowired
    private CommentStatisticsRepository commentStatisticsRepository;

    public CommentStatisticsData getStatistics(Long userId, Long commentId) {
        CommentStatisticsData commentStatistics = commentStatisticsRepository.findByCommentId(commentId);
        if (commentStatistics == null) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND);
        }
        CommentLikeData commentLikeData = commentLikeRepository.findByUserIdAndCommentId(userId, commentId);
        if (commentLikeData != null) {
            commentStatistics.setUserLikeType(commentLikeData.getType());
        }
        return commentStatistics;
    }

    public CommentStatisticsData initialize(Long commentId) {
        if (commentStatisticsRepository.existsByCommentId(commentId)) {
            throw new ApplicationException(RepositoryError.RESOURCE_ALREADY_EXISTS);
        }
        CommentStatisticsData commentStatistics = new CommentStatisticsData();
        commentStatistics.setCommentId(commentId);
        commentStatistics.setLikesCount(0);
        commentStatistics.setDislikesCount(0);
        return commentStatisticsRepository.save(commentStatistics);
    }

    public void remove(Long commentId) {
        CommentStatisticsData commentStatistics = commentStatisticsRepository.findByCommentId(commentId);
        if (commentStatistics == null) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND);
        }
        commentStatisticsRepository.delete(commentStatistics);
    }

    @Transactional
    public CommentStatisticsData convertLike(Long commentId, boolean toLike) {
        CommentStatisticsData commentStatistics = commentStatisticsRepository.findByCommentId(commentId);
        if (commentStatistics == null) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND);
        }
        commentStatistics.setLikesCount(commentStatistics.getLikesCount() + (toLike ? 1 : -1));
        commentStatistics.setDislikesCount(commentStatistics.getDislikesCount() + (toLike ? -1 : 1));
        return commentStatistics;
    }

    @Transactional
    public CommentStatisticsData addLike(Long commentId) {
        CommentStatisticsData commentStatistics = commentStatisticsRepository.findByCommentId(commentId);
        if (commentStatistics == null) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND);
        }
        commentStatistics.setLikesCount(commentStatistics.getLikesCount() + 1);
        return commentStatistics;
    }

    @Transactional
    public CommentStatisticsData removeLike(Long commentId) {
        CommentStatisticsData commentStatistics = commentStatisticsRepository.findByCommentId(commentId);
        if (commentStatistics == null) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND);
        }
        commentStatistics.setLikesCount(commentStatistics.getLikesCount() - 1);
        return commentStatistics;
    }

    @Transactional
    public CommentStatisticsData addDislike(Long commentId) {
        CommentStatisticsData commentStatistics = commentStatisticsRepository.findByCommentId(commentId);
        if (commentStatistics == null) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND);
        }
        commentStatistics.setDislikesCount(commentStatistics.getDislikesCount() + 1);
        return commentStatistics;
    }

    @Transactional
    public CommentStatisticsData removeDislike(Long commentId) {
        CommentStatisticsData commentStatistics = commentStatisticsRepository.findByCommentId(commentId);
        if (commentStatistics == null) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND);
        }
        commentStatistics.setDislikesCount(commentStatistics.getDislikesCount() - 1);
        return commentStatistics;
    }
}
