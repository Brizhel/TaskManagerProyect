package com.taskmanager.respository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taskmanager.entity.Task;
import com.taskmanager.entity.TaskList;
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByTaskList(TaskList taskList);
    Task findByNameAndTaskList(String name, TaskList taskList);
    List<Task> findByNameContainingAndTaskList(String keyword, TaskList taskList);
    List<Task> findByNameContainingAndCompletedAndTaskList(String name, boolean completed, TaskList taskList);
}
