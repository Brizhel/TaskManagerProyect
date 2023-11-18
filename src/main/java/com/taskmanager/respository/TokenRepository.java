package com.taskmanager.respository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taskmanager.entity.Token;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Token findByToken(String token);
}
