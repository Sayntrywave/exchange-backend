package com.korotkov.exchange.dto.request;


import com.korotkov.exchange.model.TradeStatus;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TradeStatusRequest {
    @NotEmpty
    int id;
    @NotEmpty
    TradeStatus status;
}
