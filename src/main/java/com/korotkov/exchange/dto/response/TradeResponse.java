package com.korotkov.exchange.dto.response;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.korotkov.exchange.model.TradeStatus;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TradeResponse {
    private int id;

    private HouseResponse givenHouse;

    private HouseResponse receivedHouse;

    @JsonFormat(pattern="yyyy-MM-dd")
    private Date startDate;
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date endDate;

    private TradeStatus status;
}
