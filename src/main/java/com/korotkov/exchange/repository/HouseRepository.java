package com.korotkov.exchange.repository;

import com.korotkov.exchange.model.House;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface HouseRepository extends JpaRepository<House, Integer> {


    List<House> getAllByUserId(Integer userId);
    List<House> getAllByCityIgnoreCaseStartingWith(String city);

    @Query(value = "SELECT h.* " +
            "FROM houses h" +
            "         LEFT JOIN trades t ON h.id = t.given_house OR h.id = t.received_house " +
            "WHERE h.city = :city and h.house_status = 'UP_FOR_SALE'" +
            "  AND (" +
            "t IS NULL OR t.start_date > :endDate OR t.end_date < :startDate OR t.status <> 'COMPLETED')"
            , nativeQuery = true)
    List<House> getAllCitiesWithCityAndDate(String city, Date startDate, Date endDate);
    @Query("SELECT DISTINCT h.city FROM House h")
    List<String> findAllCities();

}
