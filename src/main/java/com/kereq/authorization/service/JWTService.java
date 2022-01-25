package com.kereq.authorization.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.kereq.authorization.dto.JWTTokenDTO;
import com.kereq.authorization.error.AuthError;
import com.kereq.common.util.DateUtil;
import com.kereq.main.entity.UserData;
import com.kereq.main.exception.ApplicationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class JWTService {

    private final int expirationTime;

    private final int refreshTime;

    private final String secret;

    public JWTService(@Value("${jwt.expirationTime}") int expirationTime,
               @Value("${jwt.refreshTime}") int refreshTime,
               @Value("${jwt.secret}") String secret) {
        this.expirationTime = expirationTime;
        this.refreshTime = refreshTime;
        this.secret = secret;
    }

    public String generateToken(UserData user) {
        return generateToken(user.getEmail());
    }

    public String generateRefreshToken(UserData user) {
        return generateRefreshToken(user.getEmail());
    }

    public DecodedJWT verifyToken(String token) {
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
