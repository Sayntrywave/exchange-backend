package com.korotkov.exchange.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

@Entity
@Table(name = "houses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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


    @Column(name = "total_reviews")
    private int totalReviews;

    @Column(name = "rating_sum")
    private int ratingSum;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "house_status")
    private HouseStatus status;

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
