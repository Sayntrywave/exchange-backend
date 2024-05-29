package com.korotkov.exchange.dto.response;


import com.korotkov.exchange.model.House;
import com.korotkov.exchange.model.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HouseModerationResponse {

    private int id;

    private HouseResponse house;

    private String description;

    private String city;

    private String address;

    private UserDtoResponse user;

    private Boolean IsApproved;

    private String decision;

}
