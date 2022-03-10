package com.kereq.main.service;

import com.kereq.common.error.CommonError;
import com.kereq.common.error.RepositoryError;
import com.kereq.main.entity.CommentData;
import com.kereq.main.exception.ApplicationException;
import com.kereq.main.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    private final UserService userService;

    private final PostStatisticsService postStatisticsService;

    private final CommentStatisticsService commentStatisticsService;

    public CommentService(CommentRepository commentRepository, UserService userService, PostStatisticsService postStatisticsService, CommentStatisticsService commentStatisticsService) {
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.postStatisticsService = postStatisticsService;
        this.commentStatisticsService = commentStatisticsService;
    }

    @Transactional
    public CommentData createComment(CommentData comment) { //TODO: sanitize html
        if (comment.getUser() == null) {
            throw new ApplicationException(CommonError.MISSING_ERROR, "user");
        }
        comment.setStatistics(commentStatisticsService.initialize());
        commentRepository.save(comment);
        postStatisticsService.addComment(comment.getPostId());
        return comment;
    }

    public void modifyComment(long commentId, long userId, CommentData comment) { //TODO: sanitize html
        CommentData original = commentRepository.findById(commentId)
                .orElseThrow(() -> new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND));
        if (!original.getUser().getId().equals(userId)) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND);
        }
        original.setContent(comment.getContent());
        commentRepository.save(original);
    }

    @Transactional
    public void removeComment(long commentId, long userId) {
        CommentData comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND));
        if (!comment.getUser().getId().equals(userId)) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND);
        }
        postStatisticsService.removeComment(comment.getPostId());
        commentRepository.delete(comment);
    }

    public Page<CommentData> getPostComments(long userId, long postId, Pageable page) {
        Page<CommentData> comments = commentRepository.findByPostId(postId, page);
        comments.forEach(comment -> commentStatisticsService.fillUserLikeType(userId, comment.getStatistics()));
        return comments;
    }
}
