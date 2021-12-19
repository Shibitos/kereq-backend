package com.kereq.main.controller;

import com.kereq.main.dto.FriendshipDTO;
import com.kereq.main.entity.UserDataInfo;
import com.kereq.main.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/friends")
public class FriendController {

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/invitations")
    public Page<FriendshipDTO> getInvitations(Pageable page, @AuthenticationPrincipal UserDataInfo user) {
        return userService.getInvitationsUsers(user.getId(), page).map(f -> modelMapper.map(f, FriendshipDTO.class));
    }

    @PostMapping("/invitations/{receiverId}")
    public ResponseEntity<Object> inviteFriend(@PathVariable("receiverId") Long receiverId,
                                               @AuthenticationPrincipal UserDataInfo user) {
        userService.inviteFriend(user.getId(), receiverId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/invitations/{receiverId}")
    public ResponseEntity<Object> removeInvitation(@PathVariable("receiverId") Long receiverId,
                                               @AuthenticationPrincipal UserDataInfo user) {
        userService.removeInvitation(user.getId(), receiverId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/accept/{senderId}")
    public ResponseEntity<Object> acceptInvitation(@PathVariable("senderId") Long senderId,
                                                   @AuthenticationPrincipal UserDataInfo user) {
        userService.acceptInvitation(user.getId(), senderId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reject/{senderId}")
    public ResponseEntity<Object> rejectInvitation(@PathVariable("senderId") Long senderId,
                                                   @AuthenticationPrincipal UserDataInfo user) {
        userService.rejectInvitation(user.getId(), senderId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{friendId}")
    public ResponseEntity<Object> removeFriend(@PathVariable("friendId") Long friendId,
                                               @AuthenticationPrincipal UserDataInfo user) {
        userService.removeFriend(user.getId(), friendId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public Page<FriendshipDTO> getFriends(Pageable page, @AuthenticationPrincipal UserDataInfo user) {
        return userService.getFriends(user.getId(), page).map(f -> modelMapper.map(f, FriendshipDTO.class));
    }

    @GetMapping("/{userId}")
    public Page<FriendshipDTO> getFriends(Pageable page, @PathVariable("userId") Long userId) {
        return userService.getFriends(userId, page).map(f -> modelMapper.map(f, FriendshipDTO.class));
    }
}
