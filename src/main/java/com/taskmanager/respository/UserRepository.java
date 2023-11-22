package com.taskmanager.respository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taskmanager.entity.Role;
import com.taskmanager.entity.User;
@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    User findByUsername(String username);
    User findByEmail(String email);
    User findByUsernameOrEmail(String username, String email);
	List<User> findByRolesIn(Set<Role> roles);
    List<User> findByRolesContaining(Role role);
}
