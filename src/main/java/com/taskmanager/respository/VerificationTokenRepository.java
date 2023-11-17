package com.taskmanager.respository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taskmanager.entity.VerificationToken;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    VerificationToken findByToken(String token);
}
