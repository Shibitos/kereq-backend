package com.kereq.authorization.controller;

import com.kereq.authorization.dto.ConfirmDTO;
import com.kereq.authorization.dto.ResendTokenDTO;
import com.kereq.authorization.dto.UserDTO;
import com.kereq.authorization.entity.TokenData;
import com.kereq.authorization.service.AuthService;
import com.kereq.main.entity.UserData;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(@Valid @RequestBody UserDTO userDTO) {
        UserData user = modelMapper.map(userDTO, UserData.class); //TODO: captcha?
        user = authService.registerUser(user);
        TokenData token = authService.generateVerificationToken(user);
        authService.sendVerificationToken(user, token, false);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/confirm")
    public ResponseEntity<Object> confirmUser(@Valid @RequestBody  ConfirmDTO confirmDTO) {
        authService.confirmUser(confirmDTO.getToken());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/resend-confirm")
    public ResponseEntity<Object> resendConfirmUser(@Valid @RequestBody ResendTokenDTO loginOrEmailDTO) {
        authService.resendVerificationToken(loginOrEmailDTO.getLoginOrEmail()); //TODO: security?

        return ResponseEntity.ok().build();
    }
}
