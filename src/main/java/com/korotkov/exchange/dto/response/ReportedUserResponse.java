package com.korotkov.exchange.dto.response;


import com.korotkov.exchange.model.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReportedUserResponse {

    private int id;

    private UserDtoResponse reportedUser;

    private UserDtoResponse reporter;

    private String complaintReason;

    private Boolean isRejected;
}
