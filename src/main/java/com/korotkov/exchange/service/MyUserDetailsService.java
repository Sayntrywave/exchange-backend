package com.korotkov.exchange.service;


import com.korotkov.exchange.model.User;
import com.korotkov.exchange.repository.UserRepository;
import com.korotkov.exchange.security.MyUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository repository;

    @Autowired
    public MyUserDetailsService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Optional<User> user = repository.findUserByLogin(login);


        if (user.isEmpty()) {
            throw new UsernameNotFoundException("user not found");
        }

        return new MyUserDetails(user.get());
    }
}
