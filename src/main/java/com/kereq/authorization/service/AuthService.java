package com.kereq.authorization.service;

import com.kereq.authorization.dto.UserDTO;
import com.kereq.main.entity.UserData;
import com.kereq.main.exception.ApplicationException;
import com.kereq.main.exception.error.ApplicationError;
import com.kereq.main.exception.error.RepositoryError;
import com.kereq.main.repository.RoleRepository;
import com.kereq.main.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.sonatype.aether.RepositoryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void RegisterUser(UserData user) {
        if (userRepository.existsByLogin(user.getLogin())) {
            throw new ApplicationException(RepositoryError.RESOURCE_ALREADY_EXISTS, user.getLogin(), "Login");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ApplicationException(RepositoryError.RESOURCE_ALREADY_EXISTS, user.getLogin(), "Email");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Collections.singleton(roleRepository.findByCode("ROLE_USER")));
        userRepository.save(user);
        //TODO: email verification
    }
}
