package com.kereq.main.service;

import com.kereq.main.entity.UserData;
import com.kereq.main.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserData getUserByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }
}
