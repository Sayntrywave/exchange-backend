package com.korotkov.exchange.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "surname")
    private String surname;


    @Column(name = "name")
    private String name;

    @Column(name = "login")
    private String login;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "is_in_ban")
    private Boolean isInBan;


    @OneToMany
    @JoinColumn(name = "id")
    private List<House> houses;
}
