package com.korotkov.exchange.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.util.List;


@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
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

    @Column(name = "total_reviews")
    private int totalReviews;

    @Column(name = "rating_sum")
    private int ratingSum;


    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "user_role")
    private UserRole role;


    @OneToMany
    @JoinColumn(name = "id")
    private List<House> houses;


    public void addRating(int rating) {
        ratingSum += rating;
        totalReviews++;
    }

    public void editRating(int rating) {
        ratingSum += rating;
    }

    public double getAverageRating() {
        return (double) ratingSum / totalReviews;
    }
}
