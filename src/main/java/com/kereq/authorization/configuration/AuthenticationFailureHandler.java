package com.kereq.authorization.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kereq.authorization.error.AuthError;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final ObjectMapper mapper;

    public AuthenticationFailureHandler(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;

        Map<String, Object> data = new HashMap<>();
        data.put("timestamp", new Date());
        data.put("code", httpStatus.value());
        data.put("status", httpStatus.name());
        data.put("message", AuthError.BAD_CREDENTIALS);

        response.setStatus(httpStatus.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        mapper.writeValue(response.getWriter(), data);
    }
}
