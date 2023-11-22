package com.taskmanager.respository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taskmanager.entity.*;
@Repository
public interface TaskListRepository extends JpaRepository<TaskList, Long> {
    TaskList findByName(String name);
    boolean existsByNameAndUser(String name, User user);
    TaskList findByNameAndUser(String name, User user);
    List<TaskList> findByNameContainingAndUser(String name, User user);
}
