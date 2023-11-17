package com.taskmanager.respository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taskmanager.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long>{
	Role findByName(String name);
}
