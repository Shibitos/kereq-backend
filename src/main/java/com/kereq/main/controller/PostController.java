package com.kereq.main.controller;

import com.kereq.main.dto.FindFriendDTO;
import com.kereq.main.dto.PostDTO;
import com.kereq.main.entity.PostData;
import com.kereq.main.entity.UserData;
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

    private static final int POSTS_PER_PAGE = 6; //TODO: move?

    @Autowired
    private PostService postService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/post")
    public PostDTO addPost(@Valid @RequestBody PostDTO postDTO,
                                        @AuthenticationPrincipal UserData user) {
        PostData post = modelMapper.map(postDTO, PostData.class);
        post.setUser(user);
        PostData saved = postService.createPost(post);
        return modelMapper.map(saved, PostDTO.class);
    }

    @PutMapping("/post/{id}")
    public ResponseEntity<Object> modifyPost(@Valid @RequestBody PostDTO postDTO,
                                           @PathVariable("id") Long postId,
                                           @AuthenticationPrincipal UserData user) {
        PostData post = modelMapper.map(postDTO, PostData.class);
        postService.modifyPost(postId, user.getId(), post);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/post/{id}")
    public ResponseEntity<Object> removePost(@PathVariable("id") Long postId, @AuthenticationPrincipal UserData user) {
        postService.removePost(postId, user.getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/browse")
    public Page<PostDTO> getAdsForUser(
            @PageableDefault(sort = { "auditMD" }, direction = Sort.Direction.DESC, value = POSTS_PER_PAGE)
                    Pageable page,
            @AuthenticationPrincipal UserData user) {
        return postService.getPostsForUser(user, page).map(p -> modelMapper.map(p, PostDTO.class));
    }
}
