package com.kereq.unit;

import com.kereq.authorization.entity.TokenData;
import com.kereq.authorization.error.AuthError;
import com.kereq.authorization.repository.TokenRepository;
import com.kereq.authorization.service.AuthService;
import com.kereq.helper.AssertHelper;
import com.kereq.main.entity.RoleData;
import com.kereq.main.entity.UserData;
import com.kereq.main.error.RepositoryError;
import com.kereq.main.repository.RoleRepository;
import com.kereq.main.repository.UserRepository;
import com.kereq.main.util.DateUtil;
import com.kereq.messaging.entity.MessageData;
import com.kereq.messaging.entity.MessageTemplateData;
import com.kereq.messaging.repository.MessageRepository;
import com.kereq.messaging.repository.MessageTemplateRepository;
import com.kereq.messaging.service.EmailService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

class AuthServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private MessageTemplateRepository messageTemplateRepository;

    @Mock
    private TokenRepository tokenRepository;

    @InjectMocks
    private AuthService authService;

    @Mock
    private EmailService emailService;

    private final int TOKEN_LENGTH = 36;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        when(userRepository.existsById(1L)).thenReturn(false);
        when(userRepository.existsById(2L)).thenReturn(true);
        when(userRepository.existsById(3L)).thenReturn(true);
        when(tokenRepository.existsByUserIdAndType(1L, TokenData.TokenType.VERIFICATION)).thenReturn(false);
        when(tokenRepository.existsByUserIdAndType(2L, TokenData.TokenType.VERIFICATION)).thenReturn(true);
        when(tokenRepository.existsByUserIdAndType(3L, TokenData.TokenType.VERIFICATION)).thenReturn(false);
        when(tokenRepository.save(Mockito.any(TokenData.class))).thenAnswer(i -> i.getArguments()[0]);

        MessageTemplateData template = new MessageTemplateData();
        template.setCode("COMPLETE_REGISTRATION");
        template.setBody("test{{CONFIRM_URL}}test");
        when(messageTemplateRepository.findByCode(template.getCode())).thenReturn(template);
        doNothing().when(emailService).sendMessage(Mockito.any(MessageData.class));
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    void testRegisterUser() {
        when(userRepository.existsByLoginIgnoreCase("testFound")).thenReturn(true);
        when(userRepository.existsByLoginIgnoreCase("testNotFound")).thenReturn(false);
        when(userRepository.existsByEmailIgnoreCase("testFound@abc.com")).thenReturn(true);
        when(userRepository.existsByEmailIgnoreCase("testNotFound@abc.com")).thenReturn(false);
        when(userRepository.save(Mockito.any(UserData.class))).thenAnswer(i -> i.getArguments()[0]);
        when(passwordEncoder.encode(Mockito.any(CharSequence.class))).thenReturn("encoded");
        RoleData defaultRole = new RoleData();
        defaultRole.setCode("ROLE_USER");
        when(roleRepository.findByCode("ROLE_USER")).thenReturn(defaultRole);

        UserData user = new UserData();
        user.setLogin("testFound");
        user.setEmail("testFound@abc.com");
        user.setPassword("pass");
        user.setFirstName("John");
        user.setLastName("Test");
        UserData userAtt1 = user;
        AssertHelper.assertException(RepositoryError.RESOURCE_ALREADY_EXISTS_VALUE,
                () -> authService.registerUser(userAtt1));

        user.setLogin("testNotFound");
        UserData userAtt2 = user;
        AssertHelper.assertException(RepositoryError.RESOURCE_ALREADY_EXISTS_VALUE,
                () -> authService.registerUser(userAtt2));

        user.setEmail("testNotFound@abc.com");
        UserData userAtt3 = user;
        user = Assertions.assertDoesNotThrow(() -> authService.registerUser(userAtt3));

        assertThat(user.getPassword()).isEqualTo("encoded");
        assertThat(user.getRoles().size()).isEqualTo(1);
        assertThat(user.getRoles().stream().anyMatch(r -> r.getCode().equals("ROLE_USER"))).isTrue();
        assertThat(user.isActivated()).isFalse();
    }

    @Test
    void testGenerateVerificationToken() {
        UserData user = new UserData();
        user.setId(1L);
        AssertHelper.assertException(RepositoryError.RESOURCE_NOT_FOUND,
                () -> authService.generateVerificationToken(user));
        user.setId(2L);
        AssertHelper.assertException(RepositoryError.RESOURCE_ALREADY_EXISTS,
                () -> authService.generateVerificationToken(user));
        user.setId(3L);
        user.setActivated(true);
        AssertHelper.assertException(AuthError.USER_ALREADY_ACTIVATED,
                () -> authService.generateVerificationToken(user));
        user.setActivated(false);
        TokenData token = Assertions.assertDoesNotThrow(() -> authService.generateVerificationToken(user));
        assertThat(token.getType()).isEqualTo(TokenData.TokenType.VERIFICATION);
        assertThat(token.getLastSendDate()).isNull();
        assertThat(token.getExpireDate()).isAfter(new Date());
        assertThat(token.getValue()).hasSize(TOKEN_LENGTH);
    }

    @Test
    void testRenewVerificationToken() {
        TokenData token = new TokenData();
        authService.renewVerificationToken(token);
        String oldTokenValue = token.getValue();
        assertThat(token.getValue()).hasSize(TOKEN_LENGTH);
        authService.renewVerificationToken(token);
        assertThat(token.getValue()).isNotEqualTo(oldTokenValue);
    }

    @Test
    void testSendVerificationToken() {
        MessageData message = new MessageData();
        message.setStatus(MessageData.Status.PENDING);
        when(messageRepository.findFirstByUserEmailTemplateCodeNewest("usermain@abc.com", "COMPLETE_REGISTRATION")).thenReturn(message);

        UserData userFalse = new UserData();
        userFalse.setId(1L);
        TokenData token = new TokenData();
        token.setUser(userFalse);
        token.setType(TokenData.TokenType.VERIFICATION);

        UserData userMain = new UserData();
        userMain.setId(2L);
        userMain.setEmail("usermain@abc.com");

        AssertHelper.assertException(AuthError.TOKEN_INVALID,
                () -> authService.sendVerificationToken(userMain, token, false));

        token.setUser(userMain);
        token.setType("NOTEXISTINGTOKENTYPE");
        AssertHelper.assertException(AuthError.TOKEN_INVALID,
                () -> authService.sendVerificationToken(userMain, token, false));

        token.setType(TokenData.TokenType.VERIFICATION);
        Assertions.assertDoesNotThrow(() -> authService.sendVerificationToken(userMain, token, false));
        assertThat(token.getLastSendDate()).isNotNull();
        Date lastSendDate = token.getLastSendDate();
        Assertions.assertDoesNotThrow(() -> authService.sendVerificationToken(userMain, token, true));
        assertThat(token.getLastSendDate()).isNotEqualTo(lastSendDate); //TODO: check if old message used
    }

    @Test
    void testResendVerificationToken() {
        when(userRepository.findByLoginOrEmail("nonexisting")).thenReturn(null);
        UserData user = buildUserWithId(1L);
        when(userRepository.findByLoginOrEmail("notoken")).thenReturn(user);
        when(tokenRepository.findByUserIdAndType(1L, TokenData.TokenType.VERIFICATION)).thenReturn(null);

        user = buildUserWithId(2L);
        when(userRepository.findByLoginOrEmail("resendTooEarly")).thenReturn(user);
        when(tokenRepository.findByUserIdAndType(2L, TokenData.TokenType.VERIFICATION))
                .thenReturn(buildToken(user, DateUtil.now()));

        user = buildUserWithId(3L);
        when(userRepository.findByLoginOrEmail("resend1")).thenReturn(user);
        when(tokenRepository.findByUserIdAndType(3L, TokenData.TokenType.VERIFICATION))
                .thenReturn(buildToken(user, null));

        user = buildUserWithId(4L);
        when(userRepository.findByLoginOrEmail("resend2")).thenReturn(user);
        when(tokenRepository.findByUserIdAndType(4L, TokenData.TokenType.VERIFICATION))
                .thenReturn(buildToken(user, DateUtil.addMinutes(DateUtil.now(), -AuthService.TOKEN_RESEND_TIME_MIN)));

        AssertHelper.assertException(RepositoryError.RESOURCE_NOT_FOUND_VALUE,
                () -> authService.resendVerificationToken("nonexisting"));
        AssertHelper.assertException(AuthError.TOKEN_INVALID,
                () -> authService.resendVerificationToken("notoken"));
        AssertHelper.assertException(AuthError.TOKEN_TOO_EARLY,
                () -> authService.resendVerificationToken("resendTooEarly"));

        Assertions.assertDoesNotThrow(() -> authService.resendVerificationToken("resend1"));
        Assertions.assertDoesNotThrow(() -> authService.resendVerificationToken("resend2"));
    }

    @Test
    public void testConfirmUser() {
        when(tokenRepository.findByValue("nonexisting")).thenReturn(null);

        TokenData notVerification = buildToken(null, null);
        notVerification.setType("nonexistingtype");
        when(tokenRepository.findByValue("notVerification")).thenReturn(notVerification);

        TokenData expired = buildToken(null, null);
        when(tokenRepository.findByValue("expired")).thenReturn(expired);

        UserData user = buildUserWithId(1L);
        user.setActivated(false);
        TokenData notExpired = buildToken(user, null);
        notExpired.setExpireDate(DateUtil.addMinutes(DateUtil.now(), 1000));
        when(tokenRepository.findByValue("notExpired")).thenReturn(notExpired);

        AssertHelper.assertException(AuthError.TOKEN_INVALID,
                () -> authService.confirmUser("nonexisting"));
        AssertHelper.assertException(AuthError.TOKEN_INVALID,
                () -> authService.confirmUser("notVerification"));

        AssertHelper.assertException(AuthError.TOKEN_EXPIRED,
                () -> authService.confirmUser("expired"));

        Assertions.assertDoesNotThrow(() -> authService.confirmUser("notExpired"));
        assertThat(user.isActivated()).isTrue();
    }

    private UserData buildUserWithId(Long id) {
        UserData user = new UserData();
        user.setId(id);

        return user;
    }

    private TokenData buildToken(UserData user, Date date) {
        TokenData token = new TokenData();
        token.setType(TokenData.TokenType.VERIFICATION);
        token.setLastSendDate(date);
        token.setExpireDate(DateUtil.now()); //TODO: expiration checks
        token.setUser(user);

        return token;
    }
}
