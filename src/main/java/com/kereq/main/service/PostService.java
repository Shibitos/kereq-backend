package com.kereq.main.service;

import com.kereq.common.error.CommonError;
import com.kereq.common.error.RepositoryError;
import com.kereq.main.entity.CommentData;
import com.kereq.main.entity.PostData;
import com.kereq.main.exception.ApplicationException;
import com.kereq.main.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @Autowired
    private PostStatisticsService postStatisticsService;

    @Transactional
    public PostData createPost(PostData post) { //TODO: sanitize html
        if (post.getUser() == null) { //TODO: needed?
            throw new ApplicationException(CommonError.MISSING_ERROR, "user");
        }
        PostData saved = postRepository.save(post);
        saved.setStatistics(postStatisticsService.initialize(saved.getId()));
        return saved;
    }

    public void modifyPost(long postId, long userId, PostData post) { //TODO: sanitize html
        PostData original = postRepository.findById(postId)
                .orElseThrow(() -> new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND));
        if (!original.getUser().getId().equals(userId)) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND);
        }
        original.setContent(post.getContent());
        postRepository.save(original);
    }

    @Transactional
    public void removePost(long postId, long userId) {
        PostData post = postRepository.findById(postId)
                .orElseThrow(() -> new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND));
        if (!post.getUser().getId().equals(userId)) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND);
        }
        postStatisticsService.remove(post.getId());
        postRepository.delete(post);
    }

    public Page<PostData> getBrowsePosts(long userId, Pageable page) {
        Page<PostData> posts = postRepository.findPostsForUser(userId, page);
        posts.forEach(post -> {
            userService.loadProfilePhoto(post.getUser());
            Page<CommentData> comments = commentService.getPostComments(userId, post.getId(), Pageable.ofSize(3));
            post.setComments(comments.toSet());
            post.setStatistics(postStatisticsService.getStatistics(userId, post.getId()));
        });
        return posts;
    }

    public Page<PostData> getUserPosts(long userId, Pageable page) {
        Page<PostData> posts = postRepository.findByUserId(userId, page);
        posts.forEach(post -> {
            userService.loadProfilePhoto(post.getUser());
            Page<CommentData> comments = commentService.getPostComments(userId, post.getId(), Pageable.ofSize(3));
            post.setComments(comments.toSet());
            post.setStatistics(postStatisticsService.getStatistics(userId, post.getId()));
        });
        return posts;
    }
}
