package com.korotkov.exchange.dto.response;


import com.korotkov.exchange.model.TradeStatus;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TradeResponse {
    private int id;

    private HouseResponse givenHouse;

    private HouseResponse receivedHouse;

    private Date startDate;

    private Date endDate;

    private TradeStatus status;
}
