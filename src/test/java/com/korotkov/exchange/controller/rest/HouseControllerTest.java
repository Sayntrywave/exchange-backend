package com.korotkov.exchange.controller.rest;


import com.korotkov.exchange.dto.response.HouseResponse;
import com.korotkov.exchange.dto.response.UserDtoResponse;
import com.korotkov.exchange.model.House;
import com.korotkov.exchange.model.HouseStatus;
import com.korotkov.exchange.model.User;
import com.korotkov.exchange.model.UserRole;
import com.korotkov.exchange.service.HouseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.korotkov.exchange.service.JWTService;
import com.korotkov.exchange.service.MyUserDetailsService;
import com.korotkov.exchange.service.TradeService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HouseController.class)
public class HouseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HouseService houseService;

    @MockBean
    TradeService tradeService;

    @MockBean
    private JWTService jwtService;

    @MockBean
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private House createHouse(int id, String description, String city, String address, User user, HouseStatus status) {
        return new House(id, description, city, address, user, status);
    }

    private User createUser(int id, String login, String surname, String name, int totalReviews, int ratingSum, UserRole role) {
        return new User(id, surname, name, login, null, null, null, totalReviews, ratingSum, role, null);
    }

    private HouseResponse convertToHouseResponse(House house) {
        UserDtoResponse userDtoResponse = new UserDtoResponse();
        userDtoResponse.setId(house.getUser().getId());
        userDtoResponse.setLogin(house.getUser().getLogin());
        userDtoResponse.setSurname(house.getUser().getSurname());
        userDtoResponse.setName(house.getUser().getName());
        userDtoResponse.setTotalReviews(house.getUser().getTotalReviews());
        userDtoResponse.setRatingSum(house.getUser().getRatingSum());
        userDtoResponse.setRole(house.getUser().getRole());

        HouseResponse houseResponse = new HouseResponse();
        houseResponse.setId(house.getId());
        houseResponse.setDescription(house.getDescription());
        houseResponse.setCity(house.getCity());
        houseResponse.setAddress(house.getAddress());
        houseResponse.setUser(userDtoResponse);

        return houseResponse;
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testGetAllHouses() throws Exception {
        User user = createUser(1, "user1", "Doe", "John", 5, 20, UserRole.USER);
        List<House> houses = Arrays.asList(
                createHouse(1, "Description1", "City1", "Address1", user, HouseStatus.UP_FOR_SALE),
                createHouse(2, "Description2", "City2", "Address2", user, HouseStatus.UP_FOR_SALE)
        );

        when(houseService.findAllHouses(Mockito.anyString())).thenReturn(houses);

        List<HouseResponse> houseResponses = houses.stream().map(this::convertToHouseResponse).toList();

        mockMvc.perform(get("/houses"))
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Description1"))
                .andExpect(jsonPath("$[0].city").value("City1"))
                .andExpect(jsonPath("$[0].address").value("Address1"))
                .andExpect(jsonPath("$[0].user.login").value("user1"))
                .andExpect(jsonPath("$[1].description").value("Description2"))
                .andExpect(jsonPath("$[1].city").value("City2"))
                .andExpect(jsonPath("$[1].address").value("Address2"))
                .andExpect(jsonPath("$[1].user.login").value("user1"));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testGetHouseById() throws Exception {
        User user = createUser(1, "user1", "Doe", "John", 5, 20, UserRole.USER);
        House house = createHouse(1, "Description1", "City1", "Address1", user, HouseStatus.UP_FOR_SALE);

        when(houseService.findHouseById(1)).thenReturn(house);

        HouseResponse houseResponse = convertToHouseResponse(house);

        mockMvc.perform(get("/houses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Description1"))
                .andExpect(jsonPath("$.city").value("City1"))
                .andExpect(jsonPath("$.address").value("Address1"))
                .andExpect(jsonPath("$.user.login").value("user1"));
    }

    @Test
    @WithMockUser(roles = "USER")

    public void testCreateHouse() throws Exception {
        User user = createUser(1, "user1", "Doe", "John", 5, 20, UserRole.USER);
        House house = createHouse(1, "Description1", "City1", "Address1", user, HouseStatus.UP_FOR_SALE);
        HouseResponse houseResponse = convertToHouseResponse(house);

        doNothing().when(houseService).create(any(House.class));

        mockMvc.perform(post("/houses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(houseResponse)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testUpdateHouse() throws Exception {
        User user = createUser(1, "user1", "Doe", "John", 5, 20, UserRole.USER);
        House house = createHouse(1, "Description1", "City1", "Address1", user, HouseStatus.UP_FOR_SALE);
        HouseResponse houseResponse = convertToHouseResponse(house);

        doNothing().when(houseService).edit(any(House.class), eq(1));

        mockMvc.perform(put("/houses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(houseResponse)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testDeleteHouse() throws Exception {
        doNothing().when(houseService).delete(eq(1));

        mockMvc.perform(delete("/houses/1"))
                .andExpect(status().isNoContent());

        verify(houseService, times(1)).delete(1);
    }
}
