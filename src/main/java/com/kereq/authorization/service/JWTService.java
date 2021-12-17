package com.kereq.authorization.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.kereq.authorization.dto.JWTTokenDTO;
import com.kereq.authorization.error.AuthError;
import com.kereq.main.entity.UserData;
import com.kereq.main.exception.ApplicationException;
import com.kereq.main.util.DateUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class JWTService {

    @Value("${jwt.expirationTime}")
    private int expirationTime;

    @Value("${jwt.refreshTime}")
    private int refreshTime;

    @Value("${jwt.secret}")
    private String secret;

    public String generateToken(UserData user) {
        return generateToken(user.getEmail());
    }

    public String generateRefreshToken(UserData user) {
        return generateRefreshToken(user.getEmail());
    }

    public com.auth0.jwt.interfaces.DecodedJWT verifyToken(String token) {
        return JWT.require(Algorithm.HMAC256(secret))
                .build()
                .verify(token);
    }

    public JWTTokenDTO refreshToken(String token) {
        try {
            String subject = verifyToken(token).getSubject();
            if (subject == null) {
                throw new ApplicationException(AuthError.TOKEN_INVALID);
            }
            return JWTTokenDTO.builder()
                    .accessToken(generateToken(subject))
                    .refreshToken(generateRefreshToken(subject))
                    .build();
        } catch (TokenExpiredException e) {
            throw new ApplicationException(AuthError.TOKEN_INVALID);
        }
    }

    private String generateToken(String subject) {
        return generateTokenBuilder(subject)
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationTime))
                .sign(Algorithm.HMAC256(secret));
    }

    private String generateRefreshToken(String subject) {
        return generateTokenBuilder(subject)
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshTime))
                .withJWTId(UUID.randomUUID().toString())
                .sign(Algorithm.HMAC256(secret));
    }

    private com.auth0.jwt.JWTCreator.Builder generateTokenBuilder(String subject) {
        return JWT.create()
                .withSubject(subject)
                .withIssuedAt(DateUtil.now());
    }
}
