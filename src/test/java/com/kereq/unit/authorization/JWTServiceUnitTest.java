package com.kereq.unit.authorization;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.kereq.authorization.dto.JWTTokenDTO;
import com.kereq.authorization.error.AuthError;
import com.kereq.authorization.service.JWTService;
import com.kereq.common.util.DateUtil;
import com.kereq.helper.AssertHelper;
import com.kereq.main.entity.RoleData;
import com.kereq.main.entity.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.collections.Sets;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.when;

class JWTServiceUnitTest {

    private static final int EXPIRATION_TIME = 600;
    private static final int REFRESH_TIME = 1200;
    private static final String SECRET = "test";

    private static final int MIN_EXPIRE_TIME = 1;

    private final UserDetailsService userDetailsService = Mockito.mock(UserDetailsService.class);

    private JWTService jwtService;
    private JWTService expiringJwtService;

    @BeforeEach
    public void setup() {
        jwtService = new JWTService(EXPIRATION_TIME, REFRESH_TIME, SECRET, userDetailsService);
        expiringJwtService = new JWTService(MIN_EXPIRE_TIME, MIN_EXPIRE_TIME, SECRET, userDetailsService);
    }

    @Test
    void testGenerateVerifyTokens() {
        final String email = "em@em.test";
        RoleData testRole = new RoleData(0L, "testCode", null);
        UserData user = new UserData();
        user.setEmail(email);
        user.setRoles(Sets.newSet(testRole));
        String token = jwtService.generateToken(user);
        DecodedJWT decoded = jwtService.verifyToken(token);
        assertThat(decoded.getSubject()).isEqualTo(email);
        List<String> rolesCodeList = decoded.getClaim("roles").asList(String.class);
        assertThat(rolesCodeList.size()).isOne();
        assertThat(rolesCodeList.contains(testRole.getCode())).isTrue();

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
        RoleData testRole = new RoleData(0L, "testCode", null);
        UserData user = new UserData();
        user.setRoles(Sets.newSet(testRole));
        String tokenEmpty = jwtService.generateToken(user);
        AssertHelper.assertException(AuthError.TOKEN_INVALID, () -> jwtService.refreshToken(tokenEmpty));

        user.setEmail(email);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(user);
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
