package com.korotkov.exchange.dto.response;


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
