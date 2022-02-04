package com.kereq.main.controller;

import com.kereq.main.dto.PostDTO;
import com.kereq.main.dto.PostStatisticsDTO;
import com.kereq.main.entity.PostData;
import com.kereq.main.entity.PostStatisticsData;
import com.kereq.main.entity.UserData;
import com.kereq.main.entity.UserDataInfo;
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
                                        @AuthenticationPrincipal UserDataInfo user) {
        PostData post = modelMapper.map(postDTO, PostData.class);
        post.setUser((UserData) user);
        PostData saved = postService.createPost(post);
        return modelMapper.map(saved, PostDTO.class);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> modifyPost(@Valid @RequestBody PostDTO postDTO,
                                           @PathVariable("id") long postId,
                                           @AuthenticationPrincipal UserDataInfo user) {
        PostData post = modelMapper.map(postDTO, PostData.class);
        postService.modifyPost(postId, user.getId(), post);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> removePost(@PathVariable("id") long postId, @AuthenticationPrincipal UserDataInfo user) {
        postService.removePost(postId, user.getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/browse")
    public Page<PostDTO> browsePosts(
            @PageableDefault(sort = { "auditCD" }, direction = Sort.Direction.DESC)
                    Pageable page,
            @AuthenticationPrincipal UserDataInfo user) {
        return postService.getBrowsePosts(user.getId(), page).map(p -> modelMapper.map(p, PostDTO.class));
    }

    @GetMapping("/user/{userId}")
    public Page<PostDTO> getUserPosts(
            @PageableDefault(sort = { "auditCD" }, direction = Sort.Direction.DESC)
                    Pageable page,
            @PathVariable("userId") long userId,
            @AuthenticationPrincipal UserDataInfo principal) {
        return postService.getUserPosts(userId, page).map(p -> modelMapper.map(p, PostDTO.class));
    }

    @PostMapping("/{id}/like")
    public PostStatisticsDTO addLike(@PathVariable("id") long postId,
                                     @AuthenticationPrincipal UserDataInfo user) {
        PostStatisticsData postStatistics = postLikeService.addLike(user.getId(), postId);
        return modelMapper.map(postStatistics, PostStatisticsDTO.class);
    }

    @DeleteMapping("/{id}/like")
    public PostStatisticsDTO removeLike(@PathVariable("id") long postId,
                                     @AuthenticationPrincipal UserDataInfo user) {
        PostStatisticsData postStatistics = postLikeService.removeLike(user.getId(), postId);
        return modelMapper.map(postStatistics, PostStatisticsDTO.class);
    }

    @PostMapping("/{id}/dislike")
    public PostStatisticsDTO addDislike(@PathVariable("id") long postId,
                                     @AuthenticationPrincipal UserDataInfo user) {
        PostStatisticsData postStatistics = postLikeService.addDislike(user.getId(), postId);
        return modelMapper.map(postStatistics, PostStatisticsDTO.class);
    }

    @DeleteMapping("/{id}/dislike")
    public PostStatisticsDTO removeDislike(@PathVariable("id") long postId,
                                        @AuthenticationPrincipal UserDataInfo user) {
        PostStatisticsData postStatistics = postLikeService.removeDislike(user.getId(), postId);
        return modelMapper.map(postStatistics, PostStatisticsDTO.class);
    }
}
