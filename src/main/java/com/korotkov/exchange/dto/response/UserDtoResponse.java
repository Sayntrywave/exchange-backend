package com.korotkov.exchange.dto.response;


import com.korotkov.exchange.model.UserRole;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDtoResponse {
    Integer id;
    String login;
    String surname;
    String name;
    Integer totalReviews;
    Integer ratingSum;
    String description;
    UserRole role;

}
