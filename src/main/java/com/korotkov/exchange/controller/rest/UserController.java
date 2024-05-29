package com.korotkov.exchange.controller.rest;

import com.korotkov.exchange.dto.request.ReportRequest;
import com.korotkov.exchange.dto.request.UserEditRequest;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@CrossOrigin
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/user")
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

    @PutMapping("/edit")
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

    @DeleteMapping("/delete")
    public ResponseEntity<HttpStatus> deleteUser() {
        userService.delete();

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/avatar")
    public ResponseEntity<Void> uploadUserAvatar(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.getContentType() != null && file.getContentType().startsWith("image/")) {
            System.out.println("yo this is an image");

            //todo: validate file
        }
        userService.uploadProfilePicture(file);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/avatar/{id}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<InputStreamResource> getUserAvatar(@PathVariable int id) {

        InputStreamResource myProfilePicture = userService.getProfilePicture(id);
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.IMAGE_PNG);

        //todo check image/png MediaType.valueOf()
//        headers.setContentLength(;

//        fileService.getObjects().forEach(s3Object -> System.out.println(s3Object.key()));

        return ResponseEntity.ok()
//                .headers(headers)
                .body(myProfilePicture);
    }

    @PostMapping("/report")
    public ResponseEntity<String> reportUser(@RequestBody ReportRequest request){
        userService.report(request);
        return ResponseEntity.ok("Reported!");
    }


}
