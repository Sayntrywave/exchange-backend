package com.korotkov.exchange.controller.rest;

import com.korotkov.exchange.dto.request.AuthenticationRequest;
import com.korotkov.exchange.dto.request.RegistrationRequest;
import com.korotkov.exchange.dto.response.LoginResponse;
import com.korotkov.exchange.model.EmailUser;
import com.korotkov.exchange.model.User;
import com.korotkov.exchange.service.JWTService;
import com.korotkov.exchange.service.MailSenderService;
import com.korotkov.exchange.service.RegistrationService;
import com.korotkov.exchange.service.UserService;
import com.korotkov.exchange.util.BadRequestException;
import com.korotkov.exchange.util.InvalidRequestException;
import com.korotkov.exchange.util.UserNotCreatedException;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthController {

    final UserService userService;
    final AuthenticationManager authenticationManager;
    final ModelMapper modelMapper;



    final RegistrationService registrationService;

    final JWTService jwtService;


    @Autowired
    public AuthController(UserService userService, AuthenticationManager authenticationManager, ModelMapper modelMapper, RegistrationService registrationService, JWTService jwtService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.modelMapper = modelMapper;
        this.registrationService = registrationService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<HttpStatus> registration(@RequestBody @Valid RegistrationRequest user) {

        registrationService.register(modelMapper.map(user, EmailUser.class));

        return new ResponseEntity<>(HttpStatus.OK);
    }


//    @GetMapping("/activate")
//    public ResponseEntity<HttpStatus> activate(@RequestParam(value = "t") String token,
//                                               @RequestParam(value = "is-in-ban", required = false) Boolean isInBan,
//                                               @Email @RequestParam(value = "email", required = false) String email) {
//        registrationService.activate(token, isInBan, email);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid AuthenticationRequest authenticationRequest) {


        User currentUser;
        if(authenticationRequest.getEmail() != null){
            currentUser = userService.findByEmail(authenticationRequest.getEmail());
        } else {
            currentUser = userService.findByLogin(authenticationRequest.getLogin());
        }
        UsernamePasswordAuthenticationToken authInputToken = new UsernamePasswordAuthenticationToken(
                currentUser.getLogin() ,
                authenticationRequest.getPassword()
        );

        authenticationManager.authenticate(authInputToken);


        String token = jwtService.generateToken(currentUser.getLogin());


        LoginResponse map = modelMapper.map(currentUser, LoginResponse.class);
        map.setToken(token);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }



}
