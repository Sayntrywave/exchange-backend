package com.korotkov.exchange.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "trades")
@Data
@NoArgsConstructor
public class Trade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "given_house")
    private House givenHouse;

    @ManyToOne
    @JoinColumn(name = "received_house")
    private House receivedHouse;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;


    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TradeStatus status;
}
