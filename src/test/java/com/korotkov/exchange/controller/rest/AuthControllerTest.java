package com.korotkov.exchange.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.korotkov.exchange.dto.request.AuthenticationRequest;
import com.korotkov.exchange.dto.request.RegistrationRequest;
import com.korotkov.exchange.dto.response.LoginResponse;
import com.korotkov.exchange.model.EmailUser;
import com.korotkov.exchange.model.User;
import com.korotkov.exchange.service.JWTService;
import com.korotkov.exchange.service.MailSenderService;
import com.korotkov.exchange.service.RegistrationService;
import com.korotkov.exchange.service.UserService;
import com.korotkov.exchange.util.UserNotCreatedException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private ModelMapper modelMapper;

    @MockBean
    private MailSenderService mailSenderService;

    @MockBean
    private RegistrationService registrationService;

    @MockBean
    private JWTService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testRegisterSuccess() throws Exception {
        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.setEmail("test@example.com");
        registrationRequest.setLogin("testuser");
        registrationRequest.setPassword("password");

        Mockito.when(registrationService.register(any(EmailUser.class))).thenReturn("testToken");

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isOk());

        Mockito.verify(mailSenderService).send(anyString(), anyString(), anyString());
    }

    @Test
    public void testRegisterValidationError() throws Exception {
        RegistrationRequest registrationRequest = new RegistrationRequest();

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testActivateSuccess() throws Exception {
        mockMvc.perform(get("/activate")
                        .param("t", "testToken"))
                .andExpect(status().isOk());

        Mockito.verify(registrationService).activate("testToken", null, null);
    }

    @Test
    public void testLoginSuccess() throws Exception {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setLogin("testuser");
        authenticationRequest.setPassword("password");

        User user = new User();
        user.setLogin("testuser");

        Mockito.when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);

        Mockito.when(userService.findByLogin("testuser")).thenReturn(user);

        Mockito.when(jwtService.generateToken("testuser")).thenReturn("testToken");

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken("testToken");
        loginResponse.setLogin("testuser");

        Mockito.when(modelMapper.map(any(User.class), any(Class.class))).thenReturn(loginResponse);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("testToken"))
                .andExpect(jsonPath("$.login").value("testuser"));
    }

    @Test
    public void testLoginBadCredentials() throws Exception {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setLogin("testuser");
        authenticationRequest.setPassword("wrongpassword");

        Mockito.when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
                .andExpect(status().isUnauthorized());
    }
}
