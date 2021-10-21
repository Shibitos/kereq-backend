package com.kereq.authorization.service;

import com.kereq.authorization.entity.TokenData;
import com.kereq.authorization.repository.TokenRepository;
import com.kereq.main.entity.UserData;
import com.kereq.main.exception.ApplicationException;
import com.kereq.main.exception.error.RepositoryError;
import com.kereq.main.repository.RoleRepository;
import com.kereq.main.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
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
    private PasswordEncoder passwordEncoder;

    private final int verificationTokenExpirationTime = 60 * 24; //TODO: move to app parameters (and create them)

    public UserData registerUser(UserData user) {
        if (userRepository.existsByLogin(user.getLogin())) {
            throw new ApplicationException(RepositoryError.RESOURCE_ALREADY_EXISTS, user.getLogin(), "Login");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ApplicationException(RepositoryError.RESOURCE_ALREADY_EXISTS, user.getLogin(), "Email");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Collections.singleton(roleRepository.findByCode("ROLE_USER")));
        user.setActivated(false);

        return userRepository.save(user);
        //TODO: email verification
    }

    public TokenData createVerificationToken(UserData user) {
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

    private Date calculateExpireDate(final int expireTimeInMinutes) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.add(Calendar.MINUTE, expireTimeInMinutes);
        return new Date(calendar.getTime().getTime());
    }
}
