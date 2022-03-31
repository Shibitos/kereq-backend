package com.kereq.authorization.service;

import com.kereq.authorization.entity.TokenData;
import com.kereq.authorization.error.AuthError;
import com.kereq.authorization.repository.TokenRepository;
import com.kereq.common.error.RepositoryError;
import com.kereq.common.error.ValidationError;
import com.kereq.common.util.DateUtil;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final TokenRepository tokenRepository;

    private final MessageTemplateRepository messageTemplateRepository;

    private final MessageRepository messageRepository;

    private final PasswordEncoder passwordEncoder;

    private final EmailService emailService;

    private final String frontendUrl;

    public static final String VERIFICATION_TEMPLATE_CODE = "COMPLETE_REGISTRATION";

    public static final int TOKEN_EXPIRATION_TIME_MIN = 60 * 24; //TODO: move to app parameters (and create them)

    public static final int TOKEN_RESEND_TIME_MIN = 1; //TODO: always lower than tokenExpirationTime

    public AuthService(UserRepository userRepository, RoleRepository roleRepository, TokenRepository tokenRepository, MessageTemplateRepository messageTemplateRepository, MessageRepository messageRepository, PasswordEncoder passwordEncoder, EmailService emailService, @Value("${frontend.url}") String frontendUrl) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.tokenRepository = tokenRepository;
        this.messageTemplateRepository = messageTemplateRepository;
        this.messageRepository = messageRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.frontendUrl = frontendUrl;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public TokenData registerUser(UserData user) {
        user = createUser(user); //TODO: test without reassigning
        return generateVerificationToken(user);
    }

    private UserData createUser(UserData user) {
        if (userRepository.existsByEmailIgnoreCase(user.getEmail())) {
            throw new ApplicationException(RepositoryError.RESOURCE_ALREADY_EXISTS);
        }
        if (user.getBirthDate().after(DateUtil.now())) {
            throw new ApplicationException(ValidationError.DATE_NOT_PAST);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Collections.singleton(getDefaultRole()));
        user.setActivated(false);

        return userRepository.save(user);
    }

    public RoleData getDefaultRole() {
        return roleRepository.findByCode("ROLE_USER"); //TODO: param default role?
    }

    private TokenData generateVerificationToken(UserData user) {
        TokenData token = new TokenData();
        token.setType(TokenData.TokenType.VERIFICATION);
        token.setUser(user);
        token.setValue(UUID.randomUUID());
        token.setExpireDate(DateUtil.addMinutes(DateUtil.now(), TOKEN_EXPIRATION_TIME_MIN));
        return tokenRepository.save(token);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void sendVerificationToken(UserData user, TokenData token, boolean useExistingMessage) {
        if (!token.getUser().getId().equals(user.getId())
                || !TokenData.TokenType.VERIFICATION.equals(token.getType())) {
            throw new ApplicationException(AuthError.TOKEN_INVALID);
        }
        MessageData message = null;
        if (useExistingMessage) {
            message = messageRepository
                    .findFirstByUserEmailTemplateCodeNewest(user.getEmail(), VERIFICATION_TEMPLATE_CODE);
        }
        if (message == null || !MessageData.Status.PENDING.equals(message.getStatus())) {
            message = generateVerificationMessage(user, token);
        }
        emailService.sendMessage(message);
        token.setLastSendDate(DateUtil.now());
        tokenRepository.save(token);
    }

    public void resendVerificationToken(String email) {
        UserData user = userRepository.findByEmailIgnoreCase(email);
        if (user == null) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND);
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

    private TokenData renewVerificationToken(TokenData token) {
        token.setValue(UUID.randomUUID());
        return tokenRepository.save(token);
    }

    @Transactional
    public void confirmUser(UUID token) {
        TokenData verificationToken = tokenRepository.findByValue(token);
        if (verificationToken == null || !TokenData.TokenType.VERIFICATION.equals(verificationToken.getType())) {
            throw new ApplicationException(AuthError.TOKEN_INVALID);
        }
        UserData user = verificationToken.getUser();
        if (DateUtil.isExpired(verificationToken.getExpireDate())) {
            tokenRepository.delete(verificationToken);
            throw new ApplicationException(AuthError.TOKEN_EXPIRED);
        }
        user.setActivated(true);
        tokenRepository.delete(verificationToken);
    }

    private MessageData generateVerificationMessage(UserData user, TokenData token) {
        MessageTemplateData template = messageTemplateRepository.findByCode(VERIFICATION_TEMPLATE_CODE);
        if (template == null) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND_VALUE, VERIFICATION_TEMPLATE_CODE);
        }
        Map<String, String> params = new HashMap<>();
        params.put("CONFIRM_URL", frontendUrl + "/confirm-account/" + token.getValue());
        return emailService.createMessageFromTemplate(template, user.getEmail(), params);
    }
}
