package com.taskmanager.respository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taskmanager.entity.User;

public interface UserRepository extends JpaRepository<User, Long>{
    User findByUsername(String username);
    Optional<User> findByEmail(String email);
    User findByUsernameOrEmail(String username, String email);
}
