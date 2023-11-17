package com.taskmanager.respository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.taskmanager.entity.*;

public interface TaskListRepository extends JpaRepository<TaskList, Long> {
    TaskList findByName(String name);
}
