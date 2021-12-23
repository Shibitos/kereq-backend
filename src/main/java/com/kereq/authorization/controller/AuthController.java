package com.kereq.authorization.controller;

import com.kereq.authorization.dto.ConfirmDTO;
import com.kereq.authorization.dto.JWTTokenDTO;
import com.kereq.authorization.dto.RegistrationDTO;
import com.kereq.authorization.dto.ResendTokenDTO;
import com.kereq.authorization.entity.TokenData;
import com.kereq.authorization.service.AuthService;
import com.kereq.authorization.service.JWTService;
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
    private JWTService jwtService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(@Valid @RequestBody RegistrationDTO registrationDTO) {
        UserData user = modelMapper.map(registrationDTO, UserData.class); //TODO: captcha?
        TokenData token = authService.registerUser(user);
        authService.sendVerificationToken(user, token, false);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/confirm")
    public ResponseEntity<Object> confirmUser(@Valid @RequestBody ConfirmDTO confirmDTO) {
        authService.confirmUser(confirmDTO.getToken());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/resend-confirm")
    public ResponseEntity<Object> resendConfirmUser(@Valid @RequestBody ResendTokenDTO resendTokenDTO) {
        authService.resendVerificationToken(resendTokenDTO.getEmail()); //TODO: security?

        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh-token")
    public JWTTokenDTO refreshToken(@RequestBody JWTTokenDTO jwtTokenDTO) {
        return jwtService.refreshToken(jwtTokenDTO.getRefreshToken());
    }
}
