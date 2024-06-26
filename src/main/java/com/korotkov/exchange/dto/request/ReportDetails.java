package com.korotkov.exchange.dto.request;


import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReportDetails {
    @NotNull
    private Integer id;
    @NotNull
    private Integer userId;
}
