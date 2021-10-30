package com.kereq.authorization.service;

import com.kereq.main.entity.UserData;
import com.kereq.main.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("UserDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        UserData user = userRepo.findByLogin(s); //TODO: privileges?
        if (user == null) {
            throw new UsernameNotFoundException("User with that login not found");
        }

        return user;
    }
}
