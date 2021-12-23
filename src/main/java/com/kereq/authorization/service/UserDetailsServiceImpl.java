package com.kereq.authorization.service;

import com.kereq.common.error.RepositoryError;
import com.kereq.main.entity.UserData;
import com.kereq.main.exception.ApplicationException;
import com.kereq.main.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("UserDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        UserData user = userRepository.findByEmailIgnoreCase(s); //TODO: privileges?
        if (user == null) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND);
        }

        return user;
    }
}
