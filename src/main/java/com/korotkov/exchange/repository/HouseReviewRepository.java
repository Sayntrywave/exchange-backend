package com.korotkov.exchange.repository;


import com.korotkov.exchange.model.House;
import com.korotkov.exchange.model.HouseReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HouseReviewRepository  extends JpaRepository<HouseReview, Integer> {
    List<HouseReview> findAllByHouse_Id(int houseId);
}
