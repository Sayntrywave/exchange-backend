package com.korotkov.exchange.controller.rest;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.korotkov.exchange.service.RegistrationService;
import com.korotkov.exchange.util.BadRequestException;
import com.korotkov.exchange.util.UserNotFoundException;
import jakarta.validation.constraints.Email;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ConfirmationController {

    final RegistrationService registrationService;

    public ConfirmationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping("/activate")
    public String activate(@RequestParam(value = "t") String token,
                           @RequestParam(value = "is-in-ban", required = false) Boolean isInBan,
                           @Email @RequestParam(value = "email", required = false) String email) {
       registrationService.activate(token, isInBan, email);

        return "confirm";
    }

    @ExceptionHandler
    public String handleException(BadRequestException e){
        return "already_confirmed";
    }
    @ExceptionHandler
    public String handleException(TokenExpiredException e){
        return "invalid_link";
    }
}
