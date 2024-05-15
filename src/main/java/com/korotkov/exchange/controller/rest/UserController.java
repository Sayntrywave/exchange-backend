package com.korotkov.exchange.controller.rest;

import com.korotkov.exchange.dto.request.UserEditRequest;
import com.korotkov.exchange.dto.response.UserDtoResponse;
import com.korotkov.exchange.model.User;
import com.korotkov.exchange.service.JWTService;
import com.korotkov.exchange.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    UserService userService;
    ModelMapper modelMapper;

    JWTService jwtService;

    @Autowired
    public UserController(UserService userService, ModelMapper modelMapper, JWTService jwtService) {
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.jwtService = jwtService;
    }

    @PutMapping("/user/edit")
    public ResponseEntity<Map<String, String>> editUser(@RequestBody @Valid UserEditRequest userEditRequest,
                                                        BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new BadCredentialsException(bindingResult.getFieldError().getField() + " " + bindingResult.getFieldError().getDefaultMessage());
        }
        User map = modelMapper.map(userEditRequest, User.class);

        boolean update = userService.update(map);
        if (update) {
            String token = jwtService.generateToken(userEditRequest.getLogin());
            return new ResponseEntity<>(Map.of("token", token), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/user/delete")
    public ResponseEntity<HttpStatus> deleteUser() {
        userService.delete();

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
