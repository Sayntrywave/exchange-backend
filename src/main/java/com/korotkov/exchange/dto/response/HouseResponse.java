package com.korotkov.exchange.dto.response;


import com.korotkov.exchange.model.HouseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HouseResponse {

    private int id;

    private String description;

    private String city;

    private String address;


    private int totalReviews;

    private int ratingSum;

    private UserDtoResponse user;

    private HouseStatus status;

}
