package com.kereq.authorization.service;

import com.kereq.authorization.entity.TokenData;
import com.kereq.authorization.error.AuthError;
import com.kereq.authorization.repository.TokenRepository;
import com.kereq.main.entity.RoleData;
import com.kereq.main.entity.UserData;
import com.kereq.main.error.RepositoryError;
import com.kereq.main.exception.ApplicationException;
import com.kereq.main.repository.RoleRepository;
import com.kereq.main.repository.UserRepository;
import com.kereq.main.util.DateUtil;
import com.kereq.messaging.entity.MessageData;
import com.kereq.messaging.entity.MessageTemplateData;
import com.kereq.messaging.repository.MessageRepository;
import com.kereq.messaging.repository.MessageTemplateRepository;
import com.kereq.messaging.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private MessageTemplateRepository messageTemplateRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Value("${frontend.url}")
    private String frontendUrl;

    public static final String VERIFICATION_TEMPLATE_CODE = "COMPLETE_REGISTRATION";

    public static final int TOKEN_EXPIRATION_TIME_MIN = 60 * 24; //TODO: move to app parameters (and create them)

    public static final int TOKEN_RESEND_TIME_MIN = 1; //TODO: always lower than tokenExpirationTime

    @Transactional
    public UserData registerUser(UserData user) {
        if (userRepository.existsByEmailIgnoreCase(user.getEmail())) {
            throw new ApplicationException(RepositoryError.RESOURCE_ALREADY_EXISTS_VALUE, user.getEmail(), "Email");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Collections.singleton(getDefaultRole()));
        user.setActivated(false);

        return userRepository.save(user);
    }

    public RoleData getDefaultRole() {
        return roleRepository.findByCode("ROLE_USER"); //TODO: param default role?
    }

    public TokenData generateVerificationToken(UserData user) {
        if (!userRepository.existsById(user.getId())) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND_ID, user.getId());
        }
        if (user.isActivated()) {
            throw new ApplicationException(AuthError.USER_ALREADY_ACTIVATED);
        }
        if (tokenRepository.existsByUserIdAndType(user.getId(), TokenData.TokenType.VERIFICATION)) {
            throw new ApplicationException(RepositoryError.RESOURCE_ALREADY_EXISTS);
        }
        TokenData token = new TokenData();
        token.setType(TokenData.TokenType.VERIFICATION);
        token.setUser(user);
        token.setValue(UUID.randomUUID().toString());
        token.setExpireDate(DateUtil.addMinutes(DateUtil.now(), TOKEN_EXPIRATION_TIME_MIN));

        return tokenRepository.save(token);
    }

    public void sendVerificationToken(UserData user, TokenData token, boolean useExistingMessage) {
        if (!token.getUser().getId().equals(user.getId())
                || !TokenData.TokenType.VERIFICATION.equals(token.getType())) {
            throw new ApplicationException(AuthError.TOKEN_INVALID);
        }
        MessageData message = null;
        if (useExistingMessage) {
            message = messageRepository.findFirstByUserEmailTemplateCodeNewest(user.getEmail(), VERIFICATION_TEMPLATE_CODE);
        }
        if (message == null || !MessageData.Status.PENDING.equals(message.getStatus())) {
            message = generateVerificationMessage(user, token);
        }
        emailService.sendMessage(message);
        token.setLastSendDate(DateUtil.now());
        tokenRepository.save(token);
    }

    public void resendVerificationToken(final String email) {
        UserData user = userRepository.findByEmailIgnoreCase(email);
        if (user == null) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND_VALUE, email);
        }
        TokenData token = tokenRepository.findByUserIdAndType(user.getId(), TokenData.TokenType.VERIFICATION);
        if (token == null) {
            throw new ApplicationException(AuthError.TOKEN_INVALID);
        }
        if (token.getLastSendDate() != null && !DateUtil.isExpired(token.getLastSendDate(), TOKEN_RESEND_TIME_MIN)) {
            throw new ApplicationException(AuthError.TOKEN_TOO_EARLY);
        }
        boolean expired = false;
        if (DateUtil.isExpired(token.getExpireDate())) {
            token = renewVerificationToken(token);
            expired = true;
        }
        sendVerificationToken(user, token, !expired);
    }

    public TokenData renewVerificationToken(TokenData token) {
        token.setValue(UUID.randomUUID().toString());
        return tokenRepository.save(token);
    }

    public void confirmUser(String token) {
        final TokenData verificationToken = tokenRepository.findByValue(token);
        if (verificationToken == null || !TokenData.TokenType.VERIFICATION.equals(verificationToken.getType())) {
            throw new ApplicationException(AuthError.TOKEN_INVALID);
        }
        UserData user = verificationToken.getUser();
        if (DateUtil.isExpired(verificationToken.getExpireDate())) {
            tokenRepository.delete(verificationToken);
            throw new ApplicationException(AuthError.TOKEN_EXPIRED);
        }
        user.setActivated(true);
        userRepository.save(user);
        tokenRepository.delete(verificationToken);
    }

    private MessageData generateVerificationMessage(UserData user, TokenData token) {
        MessageTemplateData template = messageTemplateRepository.findByCode(VERIFICATION_TEMPLATE_CODE);
        if (template == null) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND_VALUE, VERIFICATION_TEMPLATE_CODE);
        }
        Map<String, String> params = new HashMap<>();
        params.put("CONFIRM_URL", frontendUrl + "/confirm-account?token=" + token.getValue());
        return emailService.createMessageFromTemplate(template, user.getEmail(), params);
    }
}
