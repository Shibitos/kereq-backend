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

@Service
public class CommentService {
    
    @Autowired
    private CommentRepository commentRepository;

    public CommentData createComment(CommentData comment) { //TODO: sanitize html
        if (comment.getUser() == null) {
            throw new ApplicationException(CommonError.MISSING_ERROR, "user");
        }
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

    public void removeComment(Long commentId, Long userId) {
        CommentData comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND));
        if (!comment.getUser().getId().equals(userId)) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND);
        }
        commentRepository.delete(comment);
    }

    public Page<CommentData> getPostComments(Long postId, Pageable page) {
        return commentRepository.findByPostId(postId, page);
    }
}
