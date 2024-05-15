package com.korotkov.exchange.dto.request;


import com.korotkov.exchange.model.TradeStatus;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TradeStatusRequest {
    int id;
    TradeStatus status;
}
