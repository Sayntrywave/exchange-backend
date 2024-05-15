package com.korotkov.exchange.dto.request;


import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HouseRequest {

    private String description;

    private String city;

    private String address;

}
