package com.korotkov.exchange.dto.request;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HouseRequest {

    @NotEmpty
    private String description;


    @NotEmpty
    private String city;

    @NotEmpty
    private String address;

}
