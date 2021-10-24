package com.kereq.authorization.service;

import com.kereq.authorization.entity.TokenData;
import com.kereq.authorization.error.AuthError;
import com.kereq.authorization.repository.TokenRepository;
import com.kereq.main.entity.UserData;
import com.kereq.main.error.RepositoryError;
import com.kereq.main.exception.ApplicationException;
import com.kereq.main.repository.RoleRepository;
import com.kereq.main.repository.UserRepository;
import com.kereq.messaging.entity.MessageData;
import com.kereq.messaging.entity.MessageTemplateData;
import com.kereq.messaging.repository.MessageTemplateRepository;
import com.kereq.messaging.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.*;

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
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    private final int verificationTokenExpirationTime = 60 * 24; //TODO: move to app parameters (and create them)

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
        token.setExpireDate(calculateExpireDate(verificationTokenExpirationTime));

        return tokenRepository.save(token);
    }

    public void sendVerificationToken(UserData user, TokenData token) {
        if (!token.getUser().getId().equals(user.getId())
                || !TokenData.TokenType.VERIFICATION.equals(token.getType())) {
            throw new ApplicationException();
        }
        MessageTemplateData template = messageTemplateRepository.findByCode("COMPLETE_REGISTRATION");
        if (template == null) {
            throw new ApplicationException();
        }
        Map<String, String> params = new HashMap<>();
        final String baseUrl =
                ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        params.put("CONFIRM_URL", baseUrl + "/auth/confirm?token=" + token.getValue());
        MessageData message = emailService.createMessageFromTemplate(template, user.getEmail(), params);
        emailService.sendMessage(message);
    }

    public TokenData generateNewVerificationToken(final String existingToken) {
        TokenData token = tokenRepository.findByValue(existingToken);
        token.setValue(UUID.randomUUID().toString());
        token = tokenRepository.save(token);
        return token;
    }

    public void confirmUser(String token) {
        final TokenData verificationToken = tokenRepository.findByValue(token);
        if (verificationToken == null) {
            throw new ApplicationException(AuthError.TOKEN_INVALID);
        }

        final UserData user = verificationToken.getUser();
        final Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpireDate()
                .getTime() - cal.getTime()
                .getTime()) <= 0) {
            tokenRepository.delete(verificationToken);
            throw new ApplicationException(AuthError.TOKEN_EXPIRED);
        }

        user.setActivated(true);
        userRepository.save(user);
        tokenRepository.delete(verificationToken);
    }

    private Date calculateExpireDate(final int expireTimeInMinutes) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.add(Calendar.MINUTE, expireTimeInMinutes);
        return new Date(calendar.getTime().getTime());
    }
}
