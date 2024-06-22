package com.korotkov.exchange.dto.request;


import com.korotkov.exchange.util.AtLeastOneNotNull;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AtLeastOneNotNull
public class AuthenticationRequest {

    @Email
    private String email;
    @Size(min = 1, max = 30, message = "your login size can't be not in range(1,30)")
    private String login;
    @NotNull
    @Size(min = 3, max = 30, message = "your password size can't be not in range(3,30)")
    private String password;
}
