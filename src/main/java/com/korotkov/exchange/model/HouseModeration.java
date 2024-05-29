package com.korotkov.exchange.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "houses_to_be_moderated")
@Data
@NoArgsConstructor
public class HouseModeration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @OneToOne
    @JoinColumn(name = "house_id")
    private House house;

    @Column
    private String description;

    @Column
    private String city;

    @Column
    private String address;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "is_approved")
    private Boolean isApproved;


    @Column(name = "decision")
    private String decision;

}
