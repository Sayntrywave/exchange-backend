package com.korotkov.exchange.controller.rest;


import com.korotkov.exchange.util.BadRequestException;
import com.korotkov.exchange.util.UserNotCreatedException;
import com.korotkov.exchange.util.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class RestExceptionHandler {
    @ExceptionHandler
    private ResponseEntity<String> handleException(BadCredentialsException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<String> handleException(UserNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<String> handleException(UserNotCreatedException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<String> handleException(BadRequestException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    //    @ExceptionHandler
//    private ResponseEntity<String> handleException(InvalidRequestException e) {
//        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
//    }
    @ExceptionHandler
    private ResponseEntity<String> handleException(MethodArgumentNotValidException e) {

        String message = e.getFieldErrors().stream()
                .map(error -> String.format("error '%s': %s ", error.getField(), error.getDefaultMessage()))
                .collect(Collectors.joining(" \n"));

        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }
}
