package com.korotkov.exchange.model;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "houses")
@Data
@NoArgsConstructor
public class House {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;


    @Column
    private String description;

    @Column
    private String city;

    @Column
    private String address;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


}
