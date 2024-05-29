package com.korotkov.exchange.repository;


import com.korotkov.exchange.model.House;
import com.korotkov.exchange.model.Trade;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Integer> {

    @Query("SELECT t FROM Trade t " +
            "WHERE (t.givenHouse = :house1 OR t.receivedHouse = :house1) " +
            "AND (t.startDate <= :endDate AND t.endDate >= :startDate)")
    List<Trade> findTradesByHouseAndDates(House house1, Date startDate, Date endDate);


    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END " +
            "FROM Trade t " +
            "WHERE (t.givenHouse = :house1 OR t.receivedHouse = :house1) " +
            "AND (t.startDate <= :endDate AND t.endDate >= :startDate)")
    boolean isHouseTradedInDates(House house1, Date startDate, Date endDate);



    @Query("SELECT count(*) FROM Trade t " +
    "WHERE (t.givenHouse.id = :house_id OR t.receivedHouse.id = :house2_id) " +
    "AND (t.startDate <= :endDate AND t.endDate >= :startDate) ")
    Integer isTradePossible(int house_id, int house2_id, Date startDate, Date endDate);


    @Query("SELECT t FROM Trade t WHERE t.givenHouse.user.id = :userId OR t.receivedHouse.user.id = :userId")
    List<Trade> findAllMyTrades(Integer userId);

    @Query("SELECT count(*) FROM Trade t WHERE (t.status = 'COMPLETED') AND( (t.receivedHouse.id = :house_id AND t.givenHouse.user.id = :user_id) OR (t.givenHouse.id = :house_id AND t.receivedHouse.user.id = :user_id)) ")
    Integer findAllByUserAndHouse(Integer house_id, Integer user_id);
}
