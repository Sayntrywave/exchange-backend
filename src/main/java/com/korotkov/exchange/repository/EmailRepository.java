package com.korotkov.exchange.repository;

import com.korotkov.exchange.model.EmailUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailRepository extends JpaRepository<EmailUser, Integer> {
    Optional<EmailUser> findEmailUserByEmail(String email);
}
