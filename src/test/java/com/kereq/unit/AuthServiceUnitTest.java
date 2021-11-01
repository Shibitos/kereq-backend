package com.kereq.unit;

import com.kereq.authorization.entity.TokenData;
import com.kereq.authorization.repository.TokenRepository;
import com.kereq.authorization.service.AuthService;
import com.kereq.main.entity.RoleData;
import com.kereq.main.entity.UserData;
import com.kereq.main.exception.ApplicationException;
import com.kereq.main.repository.RoleRepository;
import com.kereq.main.repository.UserRepository;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Date;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class AuthServiceUnitTest {

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
        when(userRepository.existsByLoginIgnoreCase("testFound")).thenReturn(true);
        when(userRepository.existsByLoginIgnoreCase("testNotFound")).thenReturn(false);
        when(userRepository.existsByEmailIgnoreCase("testFound@abc.com")).thenReturn(true);
        when(userRepository.existsByEmailIgnoreCase("testNotFound@abc.com")).thenReturn(false);
        when(userRepository.existsById(1L)).thenReturn(false);
        when(userRepository.existsById(2L)).thenReturn(true);
        when(userRepository.existsById(3L)).thenReturn(true);
        when(userRepository.save(Mockito.any(UserData.class))).thenAnswer(i -> i.getArguments()[0]);
        when(passwordEncoder.encode(Mockito.any(CharSequence.class))).thenReturn("encoded");
        RoleData defaultRole = new RoleData();
        defaultRole.setCode("ROLE_USER");
        when(roleRepository.findByCode("ROLE_USER")).thenReturn(defaultRole);
        when(tokenRepository.existsByUserIdAndType(1L, TokenData.TokenType.VERIFICATION)).thenReturn(false);
        when(tokenRepository.existsByUserIdAndType(2L, TokenData.TokenType.VERIFICATION)).thenReturn(true);
        when(tokenRepository.existsByUserIdAndType(3L, TokenData.TokenType.VERIFICATION)).thenReturn(false);
        when(tokenRepository.save(Mockito.any(TokenData.class))).thenAnswer(i -> i.getArguments()[0]);
        MessageData message = new MessageData();
        message.setStatus(MessageData.Status.PENDING);
        when(messageRepository.findFirstByUserEmailTemplateCodeNewest("usermain@abc.com", "COMPLETE_REGISTRATION")).thenReturn(message);
        MessageTemplateData template = new MessageTemplateData();
        template.setCode("COMPLETE_REGISTRATION");
        template.setBody("test{{CONFIRM_URL}}test");
        when(messageTemplateRepository.findByCode(template.getCode())).thenReturn(template);
        doNothing().when(emailService).sendMessage(Mockito.any(MessageData.class));
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    public void testRegisterUser() {
        UserData user = new UserData();
        user.setLogin("testFound");
        user.setEmail("testFound@abc.com");
        user.setPassword("pass");
        user.setFirstName("John");
        user.setLastName("Test");
        UserData userAtt1 = user;
        Assertions.assertThrows(ApplicationException.class, () -> authService.registerUser(userAtt1));

        user.setLogin("testNotFound");
        UserData userAtt2 = user;
        Assertions.assertThrows(ApplicationException.class, () -> authService.registerUser(userAtt2));

        user.setEmail("testNotFound@abc.com");
        UserData userAtt3 = user;
        user = Assertions.assertDoesNotThrow(() -> authService.registerUser(userAtt3));

        assertThat(user.getPassword()).isEqualTo("encoded");
        assertThat(user.getRoles().size() == 1).isTrue();
        assertThat(user.getRoles().stream().anyMatch(r -> r.getCode().equals("ROLE_USER"))).isTrue();
        assertThat(user.isActivated()).isFalse();
    }

    @Test
    public void testGenerateVerificationToken() {
        UserData user = new UserData();
        user.setId(1L);
        Assertions.assertThrows(ApplicationException.class, () -> authService.generateVerificationToken(user));
        user.setId(2L);
        Assertions.assertThrows(ApplicationException.class, () -> authService.generateVerificationToken(user));
        user.setId(3L);
        user.setActivated(true);
        Assertions.assertThrows(ApplicationException.class, () -> authService.generateVerificationToken(user));
        user.setActivated(false);
        TokenData token = Assertions.assertDoesNotThrow(() -> authService.generateVerificationToken(user));
        assertThat(token.getType()).isEqualTo(TokenData.TokenType.VERIFICATION);
        assertThat(token.getLastSendDate()).isNull();
        assertThat(token.getExpireDate()).isAfter(new Date());
        assertThat(token.getValue().length()).isEqualTo(TOKEN_LENGTH);
    }

    @Test
    public void testRenewVerificationToken() {
        TokenData token = new TokenData();
        authService.renewVerificationToken(token);
        String oldTokenValue = token.getValue();
        assertThat(token.getValue().length()).isEqualTo(TOKEN_LENGTH);
        authService.renewVerificationToken(token);
        assertThat(token.getValue()).isNotEqualTo(oldTokenValue);
    }

    @Test
    public void testSendVerificationToken() {
        UserData userFalse = new UserData();
        userFalse.setId(1L);
        TokenData token = new TokenData();
        token.setUser(userFalse);
        token.setType(TokenData.TokenType.VERIFICATION);

        UserData userMain = new UserData();
        userMain.setId(2L);
        userMain.setEmail("usermain@abc.com");

        Assertions.assertThrows(ApplicationException.class,
                () -> authService.sendVerificationToken(userMain, token, false));

        token.setUser(userMain);
        token.setType("NOTEXISTINGTOKENTYPE");
        Assertions.assertThrows(ApplicationException.class,
                () -> authService.sendVerificationToken(userMain, token, false));

        token.setType(TokenData.TokenType.VERIFICATION);
        Assertions.assertDoesNotThrow(() -> authService.sendVerificationToken(userMain, token, false));
        assertThat(token.getLastSendDate()).isNotNull();
        Date lastSendDate = token.getLastSendDate();
        Assertions.assertDoesNotThrow(() -> authService.sendVerificationToken(userMain, token, true));
        assertThat(token.getLastSendDate()).isNotEqualTo(lastSendDate); //TODO: check if old message used
    }
}
