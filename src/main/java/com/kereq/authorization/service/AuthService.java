package com.kereq.authorization.service;

import com.kereq.authorization.entity.TokenData;
import com.kereq.authorization.error.AuthError;
import com.kereq.authorization.repository.TokenRepository;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {

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

    private final int verificationTokenExpirationTimeMin = 60 * 24; //TODO: move to app parameters (and create them)

    private final int minResendTokenTimeMin = 1; //TODO: always lower than tokenExpirationTime

    public UserData registerUser(UserData user) {
        if (userRepository.existsByLogin(user.getLogin().toLowerCase())) {
            throw new ApplicationException(RepositoryError.RESOURCE_ALREADY_EXISTS, user.getLogin(), "Login");
        }
        if (userRepository.existsByEmail(user.getEmail().toLowerCase())) {
            throw new ApplicationException(RepositoryError.RESOURCE_ALREADY_EXISTS, user.getLogin(), "Email");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Collections.singleton(roleRepository.findByCode("ROLE_USER")));
        user.setActivated(false);

        return userRepository.save(user);
    }

    public TokenData generateVerificationToken(UserData user) {
        if (!userRepository.existsById(user.getId())) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND, user.getId());
        }
        if (tokenRepository.existsByUserIdAndType(user.getId(), TokenData.TokenType.VERIFICATION)) {
            throw new ApplicationException(RepositoryError.RESOURCE_ALREADY_EXISTS); //TODO: what with empty exceptions?
        }
        TokenData token = new TokenData();
        token.setType(TokenData.TokenType.VERIFICATION);
        token.setUser(user);
        token.setValue(UUID.randomUUID().toString());
        token.setExpireDate(DateUtil.addMinutes(DateUtil.now(), verificationTokenExpirationTimeMin));

        return tokenRepository.save(token);
    }

    public void sendVerificationToken(UserData user, TokenData token, boolean useExistingMessage) {
        if (!token.getUser().getId().equals(user.getId())
                || !TokenData.TokenType.VERIFICATION.equals(token.getType())) {
            throw new ApplicationException();
        }
        MessageData message = null;
        if (useExistingMessage) {
            message = messageRepository.findFirstByTemplateCodeNewest("COMPLETE_REGISTRATION");
        }
        if (message == null || !MessageData.Status.PENDING.equals(message.getStatus())) {
            message = generateVerificationMessage(user, token);
        }
        emailService.sendMessage(message);
        token.setLastSendDate(DateUtil.now());
        tokenRepository.save(token);
    }

    public void resendVerificationToken(final String loginOrEmail) {
        UserData user = userRepository.findByLoginOrEmail(loginOrEmail);
        if (user == null) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND_VALUE, loginOrEmail);
        }
        TokenData token = tokenRepository.findByUserIdAndType(user.getId(), TokenData.TokenType.VERIFICATION);
        if (token == null) {
            throw new ApplicationException(AuthError.TOKEN_INVALID);
        }
        if (!DateUtil.isExpired(token.getLastSendDate(), minResendTokenTimeMin)) {
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
        token = tokenRepository.save(token);

        return token;
    }

    public void confirmUser(String token) {
        final TokenData verificationToken = tokenRepository.findByValue(token);
        if (verificationToken == null) {
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
        MessageTemplateData template = messageTemplateRepository.findByCode("COMPLETE_REGISTRATION");
        if (template == null) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND_VALUE, "COMPLETE_REGISTRATION");
        }
        Map<String, String> params = new HashMap<>();
        final String baseUrl =
                ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString(); //TODO: frontend url
        params.put("CONFIRM_URL", baseUrl + "/auth/confirm?token=" + token.getValue());
        return emailService.createMessageFromTemplate(template, user.getEmail(), params);
    }
}
