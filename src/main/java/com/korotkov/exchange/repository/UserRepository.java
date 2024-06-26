package com.korotkov.exchange.repository;


import com.korotkov.exchange.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findUserByLogin(String username);

    Optional<User> findUserByEmail(String email);

    boolean existsUserByLogin(String login);

    boolean existsUserByEmail(String email);

}
