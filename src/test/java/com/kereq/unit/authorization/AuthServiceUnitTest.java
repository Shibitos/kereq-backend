package com.kereq.unit.authorization;

import com.kereq.authorization.entity.TokenData;
import com.kereq.authorization.error.AuthError;
import com.kereq.authorization.repository.TokenRepository;
import com.kereq.authorization.service.AuthService;
import com.kereq.common.error.RepositoryError;
import com.kereq.common.error.ValidationError;
import com.kereq.common.util.DateUtil;
import com.kereq.helper.AssertHelper;
import com.kereq.main.entity.RoleData;
import com.kereq.main.entity.UserData;
import com.kereq.main.repository.RoleRepository;
import com.kereq.main.repository.UserRepository;
import com.kereq.messaging.entity.MessageData;
import com.kereq.messaging.entity.MessageTemplateData;
import com.kereq.messaging.repository.MessageRepository;
import com.kereq.messaging.repository.MessageTemplateRepository;
import com.kereq.messaging.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

class AuthServiceUnitTest {
    
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    
    private final PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
    
    private final RoleRepository roleRepository = Mockito.mock(RoleRepository.class);
    
    private final MessageRepository messageRepository = Mockito.mock(MessageRepository.class);
    
    private final MessageTemplateRepository messageTemplateRepository = Mockito.mock(MessageTemplateRepository.class);
    
    private final TokenRepository tokenRepository = Mockito.mock(TokenRepository.class);
    
    private final EmailService emailService = Mockito.mock(EmailService.class);

    private AuthService authService;

    @BeforeEach
    public void setup() {
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
        authService = new AuthService(userRepository, roleRepository, tokenRepository,
                messageTemplateRepository, messageRepository, passwordEncoder, emailService, null);
    }

    @Test
    void testRegisterUser() {
        when(userRepository.existsByEmailIgnoreCase("testFound@abc.com")).thenReturn(true);
        when(userRepository.existsByEmailIgnoreCase("testNotFound@abc.com")).thenReturn(false);
        when(userRepository.save(Mockito.any(UserData.class))).thenAnswer(i -> i.getArguments()[0]);
        when(passwordEncoder.encode(Mockito.any(CharSequence.class))).thenReturn("encoded");
        RoleData defaultRole = new RoleData();
        defaultRole.setCode("ROLE_USER");
        when(roleRepository.findByCode("ROLE_USER")).thenReturn(defaultRole); //TODO: role default param

        UserData user = new UserData();
        user.setEmail("testFound@abc.com");
        user.setPassword("pass");
        user.setFirstName("John");
        user.setLastName("Test");
        AssertHelper.assertException(RepositoryError.RESOURCE_ALREADY_EXISTS,
                () -> authService.registerUser(user));

        Date past = DateUtil.addMinutes(DateUtil.now(), -1000);
        Date future = DateUtil.addMinutes(DateUtil.now(), 1000);
        user.setEmail("testNotFound@abc.com");
        user.setBirthDate(future);
        AssertHelper.assertException(ValidationError.DATE_NOT_PAST,
                () -> authService.registerUser(user));

        user.setBirthDate(past);
        user.setActivated(true);
        TokenData token = authService.registerUser(user);

        assertThat(user.getPassword()).isEqualTo("encoded");
        assertThat(user.getRoles().size()).isEqualTo(1);
        assertThat(user.getRoles().stream().anyMatch(r -> r.getCode().equals("ROLE_USER"))).isTrue(); //TODO: param default
        assertThat(user.isActivated()).isFalse();

        assertThat(token.getType()).isEqualTo(TokenData.TokenType.VERIFICATION);
        assertThat(token.getLastSendDate()).isNull();
        assertThat(token.getExpireDate()).isAfter(new Date());
        assertThat(token.getValue()).isNotNull();
    }

    @Test
    void testSendVerificationToken() {
        final String mainUserEmail = "usermain@abc.com";
        MessageData message = new MessageData();
        message.setStatus(MessageData.Status.PENDING);
        when(messageRepository
                .findFirstByUserEmailTemplateCodeNewest(mainUserEmail, "COMPLETE_REGISTRATION"))
                .thenReturn(message);

        UserData otherOwnerUser = new UserData();
        otherOwnerUser.setId(1L);
        TokenData token = new TokenData();
        token.setUser(otherOwnerUser);
        token.setType(TokenData.TokenType.VERIFICATION);

        UserData mainUser = new UserData();
        mainUser.setId(2L);
        mainUser.setEmail(mainUserEmail);

        AssertHelper.assertException(AuthError.TOKEN_INVALID,
                () -> authService.sendVerificationToken(mainUser, token, false));

        token.setUser(mainUser);
        token.setType("NOTEXISTINGTOKENTYPE");
        AssertHelper.assertException(AuthError.TOKEN_INVALID,
                () -> authService.sendVerificationToken(mainUser, token, false));

        token.setType(TokenData.TokenType.VERIFICATION);
        authService.sendVerificationToken(mainUser, token, false);
        assertThat(token.getLastSendDate()).isNotNull();

        token.setLastSendDate(null);
        authService.sendVerificationToken(mainUser, token, true);
        assertThat(token.getLastSendDate().getTime()).isNotNull();
        Mockito.verify(messageRepository, times(1))
                .findFirstByUserEmailTemplateCodeNewest(mainUserEmail, "COMPLETE_REGISTRATION");
    }

    @Test
    void testResendVerificationToken() {
        when(userRepository.findByEmailIgnoreCase("nonexisting")).thenReturn(null);
        UserData user = buildUserWithId(1L);
        when(userRepository.findByEmailIgnoreCase("notoken")).thenReturn(user);
        when(tokenRepository.findByUserIdAndType(1L, TokenData.TokenType.VERIFICATION)).thenReturn(null);

        user = buildUserWithId(2L);
        when(userRepository.findByEmailIgnoreCase("resendTooEarly")).thenReturn(user);
        when(tokenRepository.findByUserIdAndType(2L, TokenData.TokenType.VERIFICATION))
                .thenReturn(buildToken(user, DateUtil.now()));

        user = buildUserWithId(3L);
        when(userRepository.findByEmailIgnoreCase("resend1")).thenReturn(user);
        when(tokenRepository.findByUserIdAndType(3L, TokenData.TokenType.VERIFICATION))
                .thenReturn(buildToken(user, null));

        user = buildUserWithId(4L);
        TokenData resendToken2 = buildToken(user,
                DateUtil.addMinutes(DateUtil.now(), -(AuthService.TOKEN_RESEND_TIME_MIN * 2)));
        when(userRepository.findByEmailIgnoreCase("resend2")).thenReturn(user);
        when(tokenRepository.findByUserIdAndType(4L, TokenData.TokenType.VERIFICATION))
                .thenReturn(resendToken2);

        AssertHelper.assertException(RepositoryError.RESOURCE_NOT_FOUND,
                () -> authService.resendVerificationToken("nonexisting"));
        AssertHelper.assertException(AuthError.TOKEN_INVALID,
                () -> authService.resendVerificationToken("notoken"));
        AssertHelper.assertException(AuthError.TOKEN_TOO_EARLY,
                () -> authService.resendVerificationToken("resendTooEarly"));

        authService.resendVerificationToken("resend1");
        authService.resendVerificationToken("resend2");

        UUID firstAttemptValue = resendToken2.getValue();
        resendToken2.setLastSendDate(null);
        authService.resendVerificationToken("resend2");
        assertThat(firstAttemptValue).isNotEqualTo(resendToken2.getValue());
    }

    @Test
    void testConfirmUser() {
        UUID nonExistingUUID = UUID.randomUUID();
        when(tokenRepository.findByValue(nonExistingUUID)).thenReturn(null);

        UUID notVerificationUUID = UUID.randomUUID();
        TokenData notVerificationType = buildToken(null, null);
        notVerificationType.setType("nonexistingtype");
        when(tokenRepository.findByValue(notVerificationUUID)).thenReturn(notVerificationType);

        UUID expiredUUID = UUID.randomUUID();
        TokenData expired = buildToken(null, null);
        when(tokenRepository.findByValue(expiredUUID)).thenReturn(expired);

        UUID notExpiredUUID = UUID.randomUUID();
        UserData user = buildUserWithId(1L);
        user.setActivated(false);
        TokenData notExpired = buildToken(user, null);
        notExpired.setExpireDate(DateUtil.addMinutes(DateUtil.now(), 1000));
        when(tokenRepository.findByValue(notExpiredUUID)).thenReturn(notExpired);

        AssertHelper.assertException(AuthError.TOKEN_INVALID,
                () -> authService.confirmUser(nonExistingUUID));
        AssertHelper.assertException(AuthError.TOKEN_INVALID,
                () -> authService.confirmUser(notVerificationUUID));

        AssertHelper.assertException(AuthError.TOKEN_EXPIRED,
                () -> authService.confirmUser(expiredUUID));

        authService.confirmUser(notExpiredUUID);
        assertThat(user.isActivated()).isTrue();
    }

    private UserData buildUserWithId(long id) {
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
