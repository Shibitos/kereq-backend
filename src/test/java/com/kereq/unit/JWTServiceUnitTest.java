package com.kereq.unit;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.kereq.authorization.dto.JWTTokenDTO;
import com.kereq.authorization.error.AuthError;
import com.kereq.authorization.service.JWTService;
import com.kereq.common.util.DateUtil;
import com.kereq.helper.AssertHelper;
import com.kereq.main.entity.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

class JWTServiceUnitTest {

    private static final int EXPIRATION_TIME = 600;
    private static final int REFRESH_TIME = 1200;
    private static final String SECRET = "test";

    private static final int MIN_EXPIRE_TIME = 1;

    private JWTService jwtService;
    private JWTService expiringJwtService;

    @BeforeEach
    public void setup() {
        jwtService = new JWTService(EXPIRATION_TIME, REFRESH_TIME, SECRET);
        expiringJwtService = new JWTService(MIN_EXPIRE_TIME, MIN_EXPIRE_TIME, SECRET);
    }

    @Test
    void testGenerateVerifyTokens() {
        final String email = "em@em.test";
        UserData user = new UserData();
        user.setEmail(email);
        String token = jwtService.generateToken(user);
        DecodedJWT decoded = jwtService.verifyToken(token);
        assertThat(decoded.getSubject()).isEqualTo(email);

        String expiredToken = expiringJwtService.generateToken(user);
        Date tokenIssueDate = DateUtil.now();
        await().atMost(2, TimeUnit.SECONDS)
                .until(() -> DateUtil.timeDiffSince(tokenIssueDate) > 1000 * MIN_EXPIRE_TIME);
        AssertHelper.assertException(TokenExpiredException.class, () -> jwtService.verifyToken(expiredToken));

        String refreshToken = jwtService.generateRefreshToken(user);
        decoded = jwtService.verifyToken(refreshToken);
        assertThat(decoded.getSubject()).isEqualTo(email);

        String expiredRefreshToken = expiringJwtService.generateRefreshToken(user);
        Date refreshTokenIssueDate = DateUtil.now();
        await().atMost(2, TimeUnit.SECONDS)
                .until(() -> DateUtil.timeDiffSince(refreshTokenIssueDate) > 1000 * MIN_EXPIRE_TIME);
        AssertHelper.assertException(TokenExpiredException.class, () -> jwtService.verifyToken(expiredRefreshToken));
    }

    @Test
    void testRefreshToken() {
        final String email = "em@em.test";
        UserData user = new UserData();
        String tokenEmpty = jwtService.generateToken(user);
        AssertHelper.assertException(AuthError.TOKEN_INVALID, () -> jwtService.refreshToken(tokenEmpty));

        user.setEmail(email);
        String token = jwtService.generateToken(user);

        JWTTokenDTO jwtTokenDTO = jwtService.refreshToken(token);
        DecodedJWT decodedAccess = jwtService.verifyToken(jwtTokenDTO.getAccessToken());
        DecodedJWT decodedRefresh = jwtService.verifyToken(jwtTokenDTO.getAccessToken());
        assertThat(decodedAccess.getSubject()).isEqualTo(email);
        assertThat(decodedRefresh.getSubject()).isEqualTo(email);

        String expiredToken = expiringJwtService.generateToken(user);
        Date expiredTokenIssueDate = DateUtil.now();
        await().atMost(2, TimeUnit.SECONDS)
                .until(() -> DateUtil.timeDiffSince(expiredTokenIssueDate) > 1000 * MIN_EXPIRE_TIME);
        AssertHelper.assertException(AuthError.TOKEN_INVALID, () -> jwtService.refreshToken(expiredToken));
    }
}
