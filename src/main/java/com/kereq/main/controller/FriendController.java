package com.kereq.main.controller;

import com.kereq.main.dto.FriendshipDTO;
import com.kereq.main.dto.UserDTO;
import com.kereq.main.entity.FindFriendData;
import com.kereq.main.entity.FriendshipData;
import com.kereq.main.entity.UserData;
import com.kereq.main.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Object> inviteFriend(@PathVariable("receiverId") Long receiverId,
                                               @AuthenticationPrincipal UserData user) {
        userService.inviteFriend(user.getId(), receiverId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/invite/{receiverId}")
    public ResponseEntity<Object> removeInvitation(@PathVariable("receiverId") Long receiverId,
                                               @AuthenticationPrincipal UserData user) {
        userService.removeInvitation(user.getId(), receiverId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/accept/{senderId}")
    public ResponseEntity<Object> acceptInvitation(@PathVariable("senderId") Long senderId,
                                                   @AuthenticationPrincipal UserData user) {
        userService.acceptInvitation(user.getId(), senderId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/decline/{senderId}")
    public ResponseEntity<Object> declineInvitation(@PathVariable("senderId") Long senderId,
                                                   @AuthenticationPrincipal UserData user) {
        userService.declineInvitation(user.getId(), senderId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/friend/{friendId}")
    public ResponseEntity<Object> removeFriend(@PathVariable("friendId") Long friendId,
                                               @AuthenticationPrincipal UserData user) {
        userService.removeFriend(user.getId(), friendId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/invitations")
    public Page<FriendshipDTO> getInvitations(Pageable page, @AuthenticationPrincipal UserData user) {
        return userService.getInvitationsUsers(user.getId(), page).map(this::convertToDTO);
    }

    @GetMapping("/friends")
    public Page<FriendshipDTO> getFriends(Pageable page, @AuthenticationPrincipal UserData user) {
        return userService.getFriends(user.getId(), page).map(this::convertToDTO);
    }

    private FriendshipDTO convertToDTO(FriendshipData findFriendData) {
        FriendshipDTO test = new FriendshipDTO();
        if (findFriendData.getUser() != null) {
            test.setUser(modelMapper.map(findFriendData.getUser(), UserDTO.class));
        }
        if (findFriendData.getFriend() != null) {
            test.setFriend(modelMapper.map(findFriendData.getFriend(), UserDTO.class));
        }
        test.setAuditMD(findFriendData.getAuditMD());
        return test;
//        return modelMapper.map(findFriendData, FriendshipDTO.class); //TODO: fix
    }
}
