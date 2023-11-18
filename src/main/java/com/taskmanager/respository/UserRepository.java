package com.taskmanager.respository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taskmanager.entity.User;

public interface UserRepository extends JpaRepository<User, Long>{
    User findByUsername(String username);
    User findByEmail(String email);
    User findByUsernameOrEmail(String username, String email);
}
