package com.kereq.main.controller;

import com.kereq.main.dto.UserDTO;
import com.kereq.main.entity.UserData;
import com.kereq.main.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/friends")
public class FriendController {

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/invite/{receiverId}")
    public ResponseEntity<Object> inviteFriend(@PathParam("receiverId") Long receiverId,
                                               @AuthenticationPrincipal UserData user) {
        userService.inviteFriend(user.getId(), receiverId);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/invite/{receiverId}")
    public ResponseEntity<Object> removeInvitation(@PathParam("receiverId") Long receiverId,
                                               @AuthenticationPrincipal UserData user) {
        userService.removeInvitation(user.getId(), receiverId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/accept/{senderId}")
    public ResponseEntity<Object> acceptInvitation(@PathParam("senderId") Long senderId,
                                                   @AuthenticationPrincipal UserData user) {
        userService.acceptInvitation(user.getId(), senderId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/decline/{senderId}")
    public ResponseEntity<Object> declineInvitation(@PathParam("senderId") Long senderId,
                                                   @AuthenticationPrincipal UserData user) {
        userService.declineInvitation(user.getId(), senderId);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/friend/{friendId}")
    public ResponseEntity<Object> removeFriend(@PathParam("friendId") Long friendId,
                                               @AuthenticationPrincipal UserData user) {
        userService.removeFriend(user.getId(), friendId);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/invitations")
    public List<UserDTO> getInvitations(@AuthenticationPrincipal UserData user) { //TODO: paging?
        return userService.getInvitationsUsers(user)
                .stream().map(i -> modelMapper.map(i, UserDTO.class)).collect(Collectors.toList());
    }

    @GetMapping("/friends")
    public List<UserDTO> getFriends(@AuthenticationPrincipal UserData user) { //TODO: paging
        return userService.getFriends(user)
                .stream().map(i -> modelMapper.map(i, UserDTO.class)).collect(Collectors.toList());
    }
}
