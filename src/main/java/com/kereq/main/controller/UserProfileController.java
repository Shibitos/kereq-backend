package com.kereq.main.controller;

import com.kereq.main.dto.UserDTO;
import com.kereq.main.entity.UserData;
import com.kereq.main.entity.UserDataInfo;
import com.kereq.main.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/profile")
public class UserProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    public UserDTO getLoggedUser(@AuthenticationPrincipal UserDataInfo user) {
        return modelMapper.map(user, UserDTO.class);
    }

    @GetMapping("/{userId}")
    public UserDTO getUser(@PathVariable("userId") Long userId) {
        UserData requestedUser = userService.getUser(userId);
        return modelMapper.map(requestedUser, UserDTO.class);
    }

    @PatchMapping
    public UserDTO modifyLoggedUser(@Valid @RequestBody UserDTO userDTO,
                                           @AuthenticationPrincipal UserDataInfo user) {
        UserData modifiedUser = modelMapper.map(userDTO, UserData.class);
        modifiedUser = userService.modifyUser(user.getId(), modifiedUser);
        return modelMapper.map(modifiedUser, UserDTO.class);
    }
}
