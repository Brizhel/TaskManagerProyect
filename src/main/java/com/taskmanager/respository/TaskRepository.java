package com.taskmanager.respository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taskmanager.entity.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {
    // Puedes agregar consultas personalizadas si es necesario
}
