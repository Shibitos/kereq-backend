package com.kereq.main.controller;

import com.kereq.communicator.shared.dto.ConnectionEventDTO;
import com.kereq.main.dto.FriendshipDTO;
import com.kereq.main.entity.FriendshipData;
import com.kereq.main.entity.UserDataInfo;
import com.kereq.main.sender.ConnectionEventSender;
import com.kereq.main.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/friends")
public class FriendController {

    private final UserService userService;

    private final ModelMapper modelMapper;

    private final ConnectionEventSender connectionEventSender;

    public FriendController(UserService userService, ModelMapper modelMapper, ConnectionEventSender connectionEventSender) {
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.connectionEventSender = connectionEventSender;
    }

    @GetMapping("/invitations")
    public Page<FriendshipDTO> getInvitations(Pageable page, @AuthenticationPrincipal UserDataInfo user) {
        return userService.getInvitationsUsers(user.getId(), page).map(f -> modelMapper.map(f, FriendshipDTO.class));
    }

    @PostMapping("/invitations/{receiverId}")
    public ResponseEntity<Object> inviteFriend(@PathVariable("receiverId") long receiverId,
                                               @AuthenticationPrincipal UserDataInfo user) {
        userService.inviteFriend(user.getId(), receiverId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/invitations/{receiverId}")
    public ResponseEntity<Object> removeInvitation(@PathVariable("receiverId") long receiverId,
                                                   @AuthenticationPrincipal UserDataInfo user) {
        userService.removeInvitation(user.getId(), receiverId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/accept/{senderId}")
    public ResponseEntity<Object> acceptInvitation(@PathVariable("senderId") long senderId,
                                                   @AuthenticationPrincipal UserDataInfo user) {
        userService.acceptInvitation(user.getId(), senderId);
        connectionEventSender.send(new ConnectionEventDTO(ConnectionEventDTO.Type.CONNECT, user.getId(), senderId));
        connectionEventSender.send(new ConnectionEventDTO(ConnectionEventDTO.Type.CONNECT, senderId, user.getId()));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reject/{senderId}")
    public ResponseEntity<Object> rejectInvitation(@PathVariable("senderId") long senderId,
                                                   @AuthenticationPrincipal UserDataInfo user) {
        userService.rejectInvitation(user.getId(), senderId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{friendId}")
    public ResponseEntity<Object> removeFriend(@PathVariable("friendId") long friendId,
                                               @AuthenticationPrincipal UserDataInfo user) {
        userService.removeFriend(user.getId(), friendId);
        connectionEventSender.send(new ConnectionEventDTO(ConnectionEventDTO.Type.REMOVAL, user.getId(), friendId));
        connectionEventSender.send(new ConnectionEventDTO(ConnectionEventDTO.Type.REMOVAL, friendId, user.getId()));
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public Page<FriendshipDTO> getFriends(Pageable page, @AuthenticationPrincipal UserDataInfo user) {
        return userService.getFriends(user.getId(), page).map(f -> modelMapper.map(f, FriendshipDTO.class));
    }

    @GetMapping("/online-first")
    public Page<FriendshipDTO> getFriendsOnlineFirst(
            @PageableDefault(sort = "friend.online", direction = Sort.Direction.DESC) Pageable page,
            @AuthenticationPrincipal UserDataInfo user) {
        return userService.getFriends(user.getId(), page).map(f -> modelMapper.map(f, FriendshipDTO.class));
    }

    @GetMapping("/{userId}")
    public Page<FriendshipDTO> getFriends(Pageable page, @PathVariable("userId") long userId) {
        return userService.getFriends(userId, page).map(f -> modelMapper.map(f, FriendshipDTO.class));
    }

    @GetMapping("/with/{friendId}")
    public FriendshipDTO getFriendship(@AuthenticationPrincipal UserDataInfo user,
                                       @PathVariable("friendId") long friendId) {
        FriendshipData friendship = userService.getFriendship(user.getId(), friendId);
        return modelMapper.map(friendship, FriendshipDTO.class);
    }
}
