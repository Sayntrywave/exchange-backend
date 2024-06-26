package com.korotkov.exchange.repository;


import com.korotkov.exchange.model.HouseReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HouseReviewRepository extends JpaRepository<HouseReview, Integer> {
    List<HouseReview> findAllByHouse_Id(int houseId);

    List<HouseReview> findAllByAuthor_Id(int authorId);

    @Query(value = "SELECT hr.*" +
            "FROM house_reviews hr " +
            "JOIN houses h ON hr.house_id = h.id " +
            "WHERE h.user_id = :userId", nativeQuery = true)
    List<HouseReview> findAllUsersReviews(int userId);

}
