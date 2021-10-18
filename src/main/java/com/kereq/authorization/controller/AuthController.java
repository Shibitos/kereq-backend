package com.kereq.authorization.controller;

import com.kereq.authorization.dto.UserDTO;
import com.kereq.authorization.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("/register")
    public String register() {
        return "reg";
    }

    @PostMapping("/register")
    public ResponseEntity registerUser(@Valid @RequestBody UserDTO userDTO, HttpServletRequest request) {
        authService.RegisterUser(userDTO);

        return ResponseEntity.ok().build();
    }
}
