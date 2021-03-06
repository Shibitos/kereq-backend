package com.kereq.main.controller;

import com.kereq.common.dto.BaseDTO;
import com.kereq.main.dto.CommentDTO;
import com.kereq.main.dto.CommentStatisticsDTO;
import com.kereq.main.entity.CommentData;
import com.kereq.main.entity.CommentStatisticsData;
import com.kereq.main.entity.UserData;
import com.kereq.main.entity.UserDataInfo;
import com.kereq.main.service.CommentLikeService;
import com.kereq.main.service.CommentService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/posts/{postId}/comments")
public class CommentController {

    private final CommentService commentService;

    private final CommentLikeService commentLikeService;

    private final ModelMapper modelMapper;

    public CommentController(CommentService commentService, CommentLikeService commentLikeService, ModelMapper modelMapper) {
        this.commentService = commentService;
        this.commentLikeService = commentLikeService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    public CommentDTO addComment(@Valid @RequestBody CommentDTO commentDTO,
                                 @PathVariable("postId") long postId,
                                 @AuthenticationPrincipal UserDataInfo user) {
        CommentData comment = modelMapper.map(commentDTO, CommentData.class);
        comment.setPostId(postId);
        comment.setUser((UserData) user);
        CommentData saved = commentService.createComment(comment);
        return modelMapper.map(saved, CommentDTO.class);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> modifyComment(@Valid @RequestBody CommentDTO commentDTO,
                                                @PathVariable("id") long commentId,
                                                @AuthenticationPrincipal UserDataInfo user) {
        CommentData comment = modelMapper.map(commentDTO, CommentData.class);
        commentService.modifyComment(commentId, user.getId(), comment);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> removeComment(@PathVariable("id") long commentId,
                                                @AuthenticationPrincipal UserDataInfo user) {
        commentService.removeComment(commentId, user.getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public Page<CommentDTO> getComments(
            @PageableDefault(sort = {"auditCD"}, direction = Sort.Direction.ASC)
                    Pageable page,
            @PathVariable("postId") long postId,
            @AuthenticationPrincipal UserDataInfo user) {
        return commentService.getPostComments(user.getId(), postId, page)
                .map(c -> (CommentDTO) modelMapper.map(c, CommentDTO.class).applyVariant(BaseDTO.HIDE_ID));
    }

    @PostMapping("/{id}/like")
    public CommentStatisticsDTO addLike(@PathVariable("id") long commentId,
                                        @AuthenticationPrincipal UserDataInfo user) {
        CommentStatisticsData commentStatistics = commentLikeService.addLike(user.getId(), commentId);
        return modelMapper.map(commentStatistics, CommentStatisticsDTO.class);
    }

    @DeleteMapping("/{id}/like")
    public CommentStatisticsDTO removeLike(@PathVariable("id") long commentId,
                                           @AuthenticationPrincipal UserDataInfo user) {
        CommentStatisticsData commentStatistics = commentLikeService.removeLike(user.getId(), commentId);
        return modelMapper.map(commentStatistics, CommentStatisticsDTO.class);
    }

    @PostMapping("/{id}/dislike")
    public CommentStatisticsDTO addDislike(@PathVariable("id") long commentId,
                                           @AuthenticationPrincipal UserDataInfo user) {
        CommentStatisticsData commentStatistics = commentLikeService.addDislike(user.getId(), commentId);
        return modelMapper.map(commentStatistics, CommentStatisticsDTO.class);
    }

    @DeleteMapping("/{id}/dislike")
    public CommentStatisticsDTO removeDislike(@PathVariable("id") long commentId,
                                              @AuthenticationPrincipal UserDataInfo user) {
        CommentStatisticsData commentStatistics = commentLikeService.removeDislike(user.getId(), commentId);
        return modelMapper.map(commentStatistics, CommentStatisticsDTO.class);
    }
}
