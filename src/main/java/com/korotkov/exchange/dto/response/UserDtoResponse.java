package com.korotkov.exchange.dto.response;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDtoResponse {
    Integer id;
    String login;
    String surname;
    String name;
    Integer totalReviews;
    Integer  ratingSum;

}
