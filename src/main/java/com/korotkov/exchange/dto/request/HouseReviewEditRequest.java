package com.korotkov.exchange.dto.request;


import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HouseReviewEditRequest {
    @NotNull
    Integer id;
    Integer rating;
    String description;
}
