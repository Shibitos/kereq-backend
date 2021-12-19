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
    
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostStatisticsService postStatisticsService;

    @Transactional
    public CommentData createComment(CommentData comment) { //TODO: sanitize html
        if (comment.getUser() == null) {
            throw new ApplicationException(CommonError.MISSING_ERROR, "user");
        }
        postStatisticsService.addComment(comment.getPostId());
        return commentRepository.save(comment);
    }

    public void modifyComment(Long commentId, Long userId, CommentData comment) { //TODO: sanitize html
        CommentData original = commentRepository.findById(commentId)
                .orElseThrow(() -> new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND));
        if (!original.getUser().getId().equals(userId)) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND);
        }
        original.setContent(comment.getContent());
        commentRepository.save(original);
    }

    @Transactional
    public void removeComment(Long commentId, Long userId) {
        CommentData comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND));
        if (!comment.getUser().getId().equals(userId)) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND);
        }
        commentRepository.delete(comment);
        postStatisticsService.removeComment(comment.getPostId());
    }

    public Page<CommentData> getPostComments(Long postId, Pageable page) {
        return commentRepository.findByPostId(postId, page);
    }
}
