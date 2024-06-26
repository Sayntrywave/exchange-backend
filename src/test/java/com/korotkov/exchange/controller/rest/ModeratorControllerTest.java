package com.korotkov.exchange.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.korotkov.exchange.dto.request.ModeratorDecision;
import com.korotkov.exchange.dto.request.ReportDetails;
import com.korotkov.exchange.dto.response.HouseModerationResponse;
import com.korotkov.exchange.dto.response.ReportedUserResponse;
import com.korotkov.exchange.dto.response.UserDtoResponse;
import com.korotkov.exchange.service.ModeratorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ModeratorController.class)
public class ModeratorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ModeratorService moderatorService;

    @Autowired
    private ObjectMapper objectMapper;

    private HouseModerationResponse createHouseModerationResponse(int id, String houseDescription, String houseCity, String houseAddress) {
        HouseModerationResponse response = new HouseModerationResponse();
        response.setId(id);

        response.getHouse().setId(id);
        response.getHouse().setDescription(houseDescription);
        response.getHouse().setCity(houseCity);
        response.getHouse().setAddress(houseAddress);

        return response;
    }

    private ReportedUserResponse createReportedUserResponse(int id, String reporterLogin, String reportedUserLogin) {
        ReportedUserResponse response = new ReportedUserResponse();
        response.setId(id);
        response.setReporter(new UserDtoResponse());
        response.getReporter().setId(id);
        response.getReporter().setLogin(reporterLogin);
        response.getReporter().setSurname("Doe");
        response.getReporter().setName("John");
        response.getReporter().setTotalReviews(5);
        response.getReporter().setRatingSum(20);

        response.setReportedUser(new UserDtoResponse());
        response.getReportedUser().setId(id);
        response.getReportedUser().setLogin(reportedUserLogin);
        response.getReportedUser().setSurname("Smith");
        response.getReportedUser().setName("Jane");
        response.getReportedUser().setTotalReviews(2);
        response.getReportedUser().setRatingSum(10);

        return response;
    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    public void testGetHouses() throws Exception {
        List<HouseModerationResponse> houses = Arrays.asList(
                createHouseModerationResponse(1, "House1 Description", "City1", "Address1"),
                createHouseModerationResponse(2, "House2 Description", "City2", "Address2")
        );


        mockMvc.perform(get("/moderator/houses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].house.description").value("House1 Description"))
                .andExpect(jsonPath("$[0].house.city").value("City1"))
                .andExpect(jsonPath("$[0].house.address").value("Address1"))
                .andExpect(jsonPath("$[1].house.description").value("House2 Description"))
                .andExpect(jsonPath("$[1].house.city").value("City2"))
                .andExpect(jsonPath("$[1].house.address").value("Address2"));

        verify(moderatorService, times(1)).findAllHouses();
    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    public void testSetDecision() throws Exception {
        ModeratorDecision decision = new ModeratorDecision();
        decision.setId(1);
        decision.setDecision("APPROVE");

        mockMvc.perform(put("/moderator/house")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(decision)))
                .andExpect(status().isOk());

        verify(moderatorService, times(1)).moderate(any(ModeratorDecision.class));
    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    public void testBanUser() throws Exception {
        ReportDetails reportDetails = new ReportDetails();
        reportDetails.setUserId(1);

        mockMvc.perform(put("/moderator/ban")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reportDetails)))
                .andExpect(status().isOk())
                .andExpect(content().string("banned!"));

        verify(moderatorService, times(1)).banUser(any(ReportDetails.class));
    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    public void testRejectReport() throws Exception {
        ReportDetails reportDetails = new ReportDetails();
        reportDetails.setUserId(1);

        mockMvc.perform(put("/moderator/reject")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reportDetails)))
                .andExpect(status().isOk())
                .andExpect(content().string("Rejected!"));

        verify(moderatorService, times(1)).rejectRequest(any(ReportDetails.class));
    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    public void testGetReportedUsers() throws Exception {
        List<ReportedUserResponse> reportedUsers = Arrays.asList(
                createReportedUserResponse(1, "reporter1", "reportedUser1"),
                createReportedUserResponse(2, "reporter2", "reportedUser2")
        );


        mockMvc.perform(get("/moderator/reported-users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].reporter.login").value("reporter1"))
                .andExpect(jsonPath("$[0].reportedUser.login").value("reportedUser1"))
                .andExpect(jsonPath("$[1].reporter.login").value("reporter2"))
                .andExpect(jsonPath("$[1].reportedUser.login").value("reportedUser2"));

        verify(moderatorService, times(1)).findAllReportedUsers();
    }
}
