package com.kereq.authorization.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kereq.authorization.dto.JWTTokenDTO;
import com.kereq.authorization.service.JWTService;
import com.kereq.main.entity.UserData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private ObjectMapper mapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        UserData principal = (UserData) authentication.getPrincipal();
        String accessToken = jwtService.generateToken(principal);
        String refreshToken = jwtService.generateRefreshToken(principal);
        JWTTokenDTO tokenResponse = JWTTokenDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        mapper.writeValue(response.getWriter(), tokenResponse);
    }
}
