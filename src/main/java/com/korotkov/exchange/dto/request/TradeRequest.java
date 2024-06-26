package com.korotkov.exchange.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TradeRequest {
    @NotNull
    private Integer givenHouseId;
    @NotNull
    private Integer receivedHouseId;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;
}
