package com.korotkov.exchange.repository;

import com.korotkov.exchange.model.ReportedUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportedUserRepository extends JpaRepository<ReportedUser, Integer> {
    List<ReportedUser> findAllByIsRejectedNull();
}
