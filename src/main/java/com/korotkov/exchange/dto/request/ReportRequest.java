package com.korotkov.exchange.dto.request;


import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReportRequest {

    @NotEmpty
    private String login;
    @NotEmpty
    private String complaintReason;
}
