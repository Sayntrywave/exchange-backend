package com.korotkov.exchange.dto.response;


import com.korotkov.exchange.model.User;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
public class HouseResponse {

    private int id;

    private String description;

    private String city;

    private String address;

    private UserDtoResponse user;
}
