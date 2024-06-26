package com.korotkov.exchange.dto.response;


import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HouseReviewResponse {

    Integer id;
    Integer rating;
    String description;
    UserDtoResponse userDtoResponse;
    HouseResponse houseResponse;
}
