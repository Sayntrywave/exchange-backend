package com.korotkov.exchange.service;


import com.korotkov.exchange.model.EmailUser;
import com.korotkov.exchange.model.User;
import com.korotkov.exchange.model.UserRole;
import com.korotkov.exchange.repository.EmailRepository;
import com.korotkov.exchange.repository.UserRepository;
import com.korotkov.exchange.util.BadRequestException;
import com.korotkov.exchange.util.UserNotCreatedException;
import com.korotkov.exchange.util.UserNotFoundException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegistrationService {
    @Value("${server.host}")
    String serverHost;

    final UserRepository repository;

    final EmailRepository emailRepository;

    final PasswordEncoder passwordEncoder;

    final JWTService jwtService;

    final ModelMapper modelMapper;

    final MailSenderService mailSenderService;


    @Autowired
    public RegistrationService(UserRepository repository, EmailRepository emailRepository, PasswordEncoder passwordEncoder, JWTService jwtService, ModelMapper modelMapper, MailSenderService mailSenderService) {
        this.repository = repository;
        this.emailRepository = emailRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.modelMapper = modelMapper;
        this.mailSenderService = mailSenderService;
    }

    @Transactional
    public void register(EmailUser user) {
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

        try{
            mailSenderService.send(user.getEmail(), "Регистрация", "https://"+ serverHost+ ":8080/activate?t="  + jwtService.generateToken(user.getEmail(), "email"));
            emailRepository.save(user);
        }
        catch (MailSendException e){
            throw new BadRequestException("Invalid Address: couldn't send an e-mail to " + user.getEmail());
        }
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
