package com.kereq.authorization.service;

import com.kereq.authorization.dto.UserDTO;
import com.kereq.main.entity.UserData;
import com.kereq.main.repository.RoleRepository;
import com.kereq.main.repository.UserRepository;
import org.modelmapper.ModelMapper;
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
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void RegisterUser(final UserDTO userDTO) {
        if (userRepository.existsByLogin(userDTO.getLogin())) {
            //TODO: custom exception handling (one custom exception class + list of exception messages with {0} format?)
            //ex. ref.: https://auth0.com/blog/get-started-with-custom-error-handling-in-spring-boot-java/
        }
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            //TODO: exception
        }
        UserData user = modelMapper.map(userDTO, UserData.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Collections.singleton(roleRepository.findByCode("ROLE_USER")));
        userRepository.save(user);
        //TODO: email verification
    }
}
