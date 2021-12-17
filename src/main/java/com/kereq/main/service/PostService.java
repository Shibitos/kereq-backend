package com.kereq.main.service;

import com.kereq.common.error.CommonError;
import com.kereq.common.error.RepositoryError;
import com.kereq.main.entity.PostData;
import com.kereq.main.exception.ApplicationException;
import com.kereq.main.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public PostData createPost(PostData post) { //TODO: sanitize html
        if (post.getUser() == null) { //TODO: needed?
            throw new ApplicationException(CommonError.MISSING_ERROR, "user");
        }
        return postRepository.save(post);
    }

    public void modifyPost(Long postId, Long userId, PostData post) { //TODO: sanitize html
        PostData original = postRepository.findById(postId)
                .orElseThrow(() -> new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND));
        if (!original.getUser().getId().equals(userId)) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND);
        }
        original.setContent(post.getContent());
        postRepository.save(original);
    }

    public void removePost(Long postId, Long userId) {
        PostData post = postRepository.findById(postId)
                .orElseThrow(() -> new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND));
        if (!post.getUser().getId().equals(userId)) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND);
        }
        postRepository.delete(post);
    }

    public Page<PostData> getBrowsePosts(Long userId, Pageable page) {
        return postRepository.findPostsForUser(userId, page);
    }

    public Page<PostData> getUserPosts(Long userId, Pageable page) {
        return postRepository.findByUserId(userId, page);
    }
}
