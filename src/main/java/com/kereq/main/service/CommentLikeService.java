package com.kereq.main.service;

import com.kereq.common.error.RepositoryError;
import com.kereq.main.constant.LikeType;
import com.kereq.main.entity.CommentLikeData;
import com.kereq.main.entity.CommentStatisticsData;
import com.kereq.main.exception.ApplicationException;
import com.kereq.main.repository.CommentLikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentLikeService { //TODO: abstract posts&comments?

    @Autowired
    private CommentLikeRepository commentLikeRepository;

    @Autowired
    private CommentStatisticsService commentStatisticsService;

    public CommentLikeData getUserLike(Long userId, Long commentId) {
        return commentLikeRepository.findByUserIdAndCommentId(userId, commentId);
    }

    @Transactional
    public CommentStatisticsData addLike(Long userId, Long commentId) {
        CommentLikeData commentLike = commentLikeRepository.findByUserIdAndCommentId(userId, commentId);
        if (commentLike != null) {
            if (LikeType.LIKE == commentLike.getType()) {
                throw new ApplicationException(RepositoryError.RESOURCE_ALREADY_EXISTS);
            }
            commentLike.setType(LikeType.LIKE);
            commentLikeRepository.save(commentLike);
            return commentStatisticsService.convertLike(commentId, true);
        }
        commentLike = createCommentLikeData(userId, commentId);
        commentLike.setType(LikeType.LIKE);
        commentLikeRepository.save(commentLike);
        return commentStatisticsService.addLike(commentId);
    }

    @Transactional
    public CommentStatisticsData removeLike(Long userId, Long commentId) {
        CommentLikeData commentLike = commentLikeRepository.findByUserIdAndCommentId(userId, commentId);
        if (commentLike == null || LikeType.LIKE != commentLike.getType()) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND);
        }
        commentLikeRepository.delete(commentLike);
        return commentStatisticsService.removeLike(commentId);
    }

    @Transactional
    public CommentStatisticsData addDislike(Long userId, Long commentId) {
        CommentLikeData commentLike = commentLikeRepository.findByUserIdAndCommentId(userId, commentId);
        if (commentLike != null) {
            if (LikeType.DISLIKE == commentLike.getType()) {
                throw new ApplicationException(RepositoryError.RESOURCE_ALREADY_EXISTS);
            }
            commentLike.setType(LikeType.DISLIKE);
            commentLikeRepository.save(commentLike);
            return commentStatisticsService.convertLike(commentId, false);
        }
        commentLike = createCommentLikeData(userId, commentId);
        commentLike.setType(LikeType.DISLIKE);
        commentLikeRepository.save(commentLike);
        return commentStatisticsService.addDislike(commentId);
    }


    @Transactional
    public CommentStatisticsData removeDislike(Long userId, Long commentId) {
        CommentLikeData commentLike = commentLikeRepository.findByUserIdAndCommentId(userId, commentId);
        if (commentLike == null || LikeType.DISLIKE != commentLike.getType()) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND);
        }
        commentLikeRepository.delete(commentLike);
        return commentStatisticsService.removeDislike(commentId);
    }

    private CommentLikeData createCommentLikeData(Long userId, Long commentId) {
        CommentLikeData commentLike = new CommentLikeData();
        commentLike.setCommentId(commentId);
        commentLike.setUserId(userId);
        return commentLike;
    }
}
