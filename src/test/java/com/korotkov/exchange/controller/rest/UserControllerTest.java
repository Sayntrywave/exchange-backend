package com.korotkov.exchange.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.korotkov.exchange.dto.request.ReportRequest;
import com.korotkov.exchange.dto.request.UserEditRequest;
import com.korotkov.exchange.dto.response.UserDtoResponse;
import com.korotkov.exchange.model.User;
import com.korotkov.exchange.model.UserRole;
import com.korotkov.exchange.service.JWTService;
import com.korotkov.exchange.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JWTService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private User createUser(int id, String login, String surname, String name, int totalReviews, int ratingSum, UserRole role) {
        return new User(id, surname, name, login, null, null, null, totalReviews, ratingSum, role, null);
    }

    @BeforeEach
    public void setUp() {
        Mockito.reset(userService, jwtService); // Reset mock state before each test
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testGetCurrentUser() throws Exception {
        User user = createUser(1, "user1", "Doe", "John", 5, 20, UserRole.USER);
        when(userService.getCurrentUser()).thenReturn(user);

        mockMvc.perform(get("/user/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("user1"))
                .andExpect(jsonPath("$.surname").value("Doe"))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.totalReviews").value(5))
                .andExpect(jsonPath("$.ratingSum").value(20))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testEditUser_Success() throws Exception {
        UserEditRequest editRequest = new UserEditRequest();
        editRequest.setLogin("user1");

        User mappedUser = createUser(1, "user1", "Doe", "John", 5, 20, UserRole.USER);
        when(userService.update(any(User.class))).thenReturn(true);
        when(jwtService.generateToken("user1")).thenReturn("mocked-token");

        mockMvc.perform(put("/user/edit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked-token"));

        verify(userService, times(1)).update(any(User.class));
        verify(jwtService, times(1)).generateToken("user1");
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testEditUser_BadRequest() throws Exception {
        UserEditRequest editRequest = new UserEditRequest();

        mockMvc.perform(put("/user/edit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    String errorMessage = result.getResponse().getContentAsString();
                    assert(errorMessage.contains("userEditRequest.login must not be null"));
                });

        verify(userService, never()).update(any(User.class));
        verify(jwtService, never()).generateToken(anyString());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/user/delete"))
                .andExpect(status().isOk());

        verify(userService, times(1)).delete();
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testUploadUserAvatar() throws Exception {
        // Simulate multipart file upload
        mockMvc.perform(MockMvcRequestBuilders.multipart("/user/avatar")
                        .file("file", "filename.png".getBytes()))
                .andExpect(status().isOk());

        verify(userService, times(1)).uploadProfilePicture(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testGetUserAvatar() throws Exception {
        // Assuming userService.getProfilePicture(id) returns InputStreamResource
        when(userService.getProfilePicture(anyInt())).thenReturn(null);

        mockMvc.perform(get("/user/avatar/{id}", 1))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testReportUser() throws Exception {
        ReportRequest reportRequest = new ReportRequest();
        reportRequest.setComplaintReason("Inappropriate behavior");

        mockMvc.perform(post("/user/report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reportRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Reported!"));

        verify(userService, times(1)).report(any(ReportRequest.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testReportUser_BadRequest() throws Exception {
        ReportRequest reportRequest = new ReportRequest(); // Invalid request without reason

        mockMvc.perform(post("/user/report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reportRequest)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).report(any(ReportRequest.class));
    }
}
