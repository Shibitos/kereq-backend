package com.kereq.main.controller;

import com.kereq.main.dto.UserDTO;
import com.kereq.main.entity.UserData;
import com.kereq.main.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/profile")
public class UserProfileController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/me")
    public UserDTO getLoggedUser(Principal principal) {
        UserData user = userRepository.findByEmailIgnoreCase(principal.getName());
        return modelMapper.map(user, UserDTO.class);
    }
}
