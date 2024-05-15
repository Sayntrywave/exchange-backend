package com.korotkov.exchange.repository;

import com.korotkov.exchange.model.House;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HouseRepository extends JpaRepository<House, Integer> {


    List<House> getAllByUserId(Integer userId);

    List<House> getAllByCityIgnoreCaseStartingWith(String city);

    @Query("SELECT DISTINCT h.city FROM House h")
    List<String> findAllCities();

}
