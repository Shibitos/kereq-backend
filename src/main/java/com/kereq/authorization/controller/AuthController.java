package com.kereq.authorization.controller;

import com.kereq.authorization.dto.UserDTO;
import com.kereq.authorization.service.AuthService;
import com.kereq.main.entity.UserData;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserDTO userDTO) {
        UserData user = modelMapper.map(userDTO, UserData.class);
        authService.RegisterUser(user);

        return ResponseEntity.ok().build();
    }
}
