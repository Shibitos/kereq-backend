package com.kereq.main.controller;

import com.kereq.main.dto.PostDTO;
import com.kereq.main.dto.PostStatisticsDTO;
import com.kereq.main.entity.PostData;
import com.kereq.main.entity.PostLikeData;
import com.kereq.main.entity.PostStatisticsData;
import com.kereq.main.entity.UserData;
import com.kereq.main.service.PostLikeService;
import com.kereq.main.service.PostService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private PostLikeService postLikeService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping
    public PostDTO addPost(@Valid @RequestBody PostDTO postDTO,
                                        @AuthenticationPrincipal UserData user) {
        PostData post = modelMapper.map(postDTO, PostData.class);
        post.setUser(user);
        PostData saved = postService.createPost(post);
        return modelMapper.map(saved, PostDTO.class);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> modifyPost(@Valid @RequestBody PostDTO postDTO,
                                           @PathVariable("id") Long postId,
                                           @AuthenticationPrincipal UserData user) {
        PostData post = modelMapper.map(postDTO, PostData.class);
        postService.modifyPost(postId, user.getId(), post);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> removePost(@PathVariable("id") Long postId, @AuthenticationPrincipal UserData user) {
        postService.removePost(postId, user.getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/browse")
    public Page<PostDTO> browsePosts(
            @PageableDefault(sort = { "auditCD" }, direction = Sort.Direction.DESC)
                    Pageable page,
            @AuthenticationPrincipal UserData user) {
        return postService.getBrowsePosts(user.getId(), page).map(p -> convertPostToDTO(user.getId(), p));
    }

    @GetMapping("/user/{userId}")
    public Page<PostDTO> getUserPosts(
            @PageableDefault(sort = { "auditCD" }, direction = Sort.Direction.DESC)
                    Pageable page,
            @PathVariable("userId") Long userId,
            @AuthenticationPrincipal UserData principal) {
        return postService.getUserPosts(userId, page).map(p -> convertPostToDTO(principal.getId(), p));
    }

    @PostMapping("/{id}/like")
    public PostStatisticsDTO addLike(@PathVariable("id") Long postId,
                                     @AuthenticationPrincipal UserData user) {
        PostStatisticsData postStatistics = postLikeService.addLike(user.getId(), postId);
        return modelMapper.map(postStatistics, PostStatisticsDTO.class);
    }

    @DeleteMapping("/{id}/like")
    public PostStatisticsDTO removeLike(@PathVariable("id") Long postId,
                                     @AuthenticationPrincipal UserData user) {
        PostStatisticsData postStatistics = postLikeService.removeLike(user.getId(), postId);
        return modelMapper.map(postStatistics, PostStatisticsDTO.class);
    }

    @PostMapping("/{id}/dislike")
    public PostStatisticsDTO addDislike(@PathVariable("id") Long postId,
                                     @AuthenticationPrincipal UserData user) {
        PostStatisticsData postStatistics = postLikeService.addDislike(user.getId(), postId);
        return modelMapper.map(postStatistics, PostStatisticsDTO.class);
    }

    @DeleteMapping("/{id}/dislike")
    public PostStatisticsDTO removeDislike(@PathVariable("id") Long postId,
                                        @AuthenticationPrincipal UserData user) {
        PostStatisticsData postStatistics = postLikeService.removeDislike(user.getId(), postId);
        return modelMapper.map(postStatistics, PostStatisticsDTO.class);
    }

    private PostDTO convertPostToDTO(Long userId, PostData post) {
        PostDTO postDTO = modelMapper.map(post, PostDTO.class);
        PostLikeData postLikeData = postLikeService.getUserLike(userId, post.getId());
        if (postLikeData != null) {
            postDTO.getStatistics().setUserLikeType(postLikeData.getType());
        }
        return postDTO;
    }
}
