package com.kereq.main.controller;

import com.kereq.main.dto.UserDTO;
import com.kereq.main.entity.UserData;
import com.kereq.main.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/profile")
public class UserProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/me")
    public UserDTO getLoggedUser(@AuthenticationPrincipal UserData user) {
        return modelMapper.map(user, UserDTO.class);
    }

    @GetMapping("/{userId}")
    public UserDTO getUser(@PathVariable("userId") Long userId, @AuthenticationPrincipal UserData user) {
        UserData requestedUser = userService.getUser(userId);
        return modelMapper.map(requestedUser, UserDTO.class);
    }
}
