package com.korotkov.exchange.repository;

import com.korotkov.exchange.model.HouseModeration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface HouseModerationRepository extends JpaRepository<HouseModeration, Integer> {
    List<HouseModeration> findAllByIsApprovedIsNull();
}
