package com.korotkov.exchange.service;


import com.korotkov.exchange.model.EmailUser;
import com.korotkov.exchange.model.User;
import com.korotkov.exchange.model.UserRole;
import com.korotkov.exchange.repository.EmailRepository;
import com.korotkov.exchange.repository.UserRepository;
import com.korotkov.exchange.util.UserNotCreatedException;
import com.korotkov.exchange.util.UserNotFoundException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RegistrationService {

    UserRepository repository;

    EmailRepository emailRepository;

    PasswordEncoder passwordEncoder;

    JWTService jwtService;

    ModelMapper modelMapper;


    @Autowired
    public RegistrationService(UserRepository repository, EmailRepository emailRepository, PasswordEncoder passwordEncoder, JWTService jwtService, ModelMapper modelMapper) {
        this.repository = repository;
        this.emailRepository = emailRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public String register(EmailUser user) {
        String login = user.getLogin();
        if (repository.existsUserByLogin(login)) {
            throw new UserNotCreatedException("login <" + login + "> has already been taken");
        }
        String email = user.getEmail();
        if (repository.existsUserByEmail(email)) {
            throw new UserNotCreatedException("email <" + email + "> has already been taken");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setIsInBan(false);
        emailRepository.save(user);

        return jwtService.generateToken(user.getEmail(), "email");
    }

    @Transactional
    public void activate(String token, Boolean isInBan, String email) {
        String userEmail = jwtService.validateTokenAndRetrieveClaim(token, "email");
        if (isInBan == null && email == null) {

            EmailUser emailUser = emailRepository.
                    findEmailUserByEmail(userEmail)
                    .orElseThrow(() -> new UserNotFoundException("user not found"));
            User map = modelMapper.map(emailUser, User.class);
            map.setRole(UserRole.USER);
            repository.save(map);
            emailRepository.delete(emailUser);
        } else if (isInBan != null) {
            User user = repository.findUserByEmail(userEmail)
                    .orElseThrow(() -> new UserNotFoundException("user not found"));
            user.setIsInBan(isInBan);
            repository.save(user);
        } else {
            User user = repository.findUserByEmail(userEmail)
                    .orElseThrow(() -> new UserNotFoundException("user not found"));
            user.setEmail(email);
            repository.save(user);
        }
    }

}
