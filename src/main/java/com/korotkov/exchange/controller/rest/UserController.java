package com.korotkov.exchange.controller.rest;

import com.korotkov.exchange.dto.request.ReportRequest;
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
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

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

    @GetMapping("/user/me")
    public ResponseEntity<UserDtoResponse> me() {
        return ResponseEntity.ok(modelMapper.map(userService.getCurrentUser(), UserDtoResponse.class));
    }

    @PutMapping("/user/edit")
    public ResponseEntity<Map<String, String>> editUser(@RequestBody @Valid UserEditRequest userEditRequest) {

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

    @PostMapping("/user/avatar")
    public ResponseEntity<Void> uploadUserAvatar(@RequestParam("file") MultipartFile file) throws IOException {
        userService.uploadProfilePicture(file);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/users/{id}/avatar", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<InputStreamResource> getUserAvatar(@PathVariable int id) {

        InputStreamResource myProfilePicture = userService.getProfilePicture(id);
        return ResponseEntity.ok()
                .body(myProfilePicture);
    }

    @GetMapping(value = "/users/{id}")
    public ResponseEntity<UserDtoResponse> getUser(@PathVariable int id) {
        return ResponseEntity.ok(modelMapper.map(userService.getById(id), UserDtoResponse.class));
    }


    @PostMapping("/user/report")
    public ResponseEntity<String> reportUser(@RequestBody ReportRequest request) {
        userService.report(request);
        return ResponseEntity.ok("Reported!");
    }


}
