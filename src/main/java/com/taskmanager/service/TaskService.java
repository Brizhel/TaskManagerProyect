package com.taskmanager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taskmanager.entity.Task;
import com.taskmanager.entity.TaskList;
import com.taskmanager.respository.TaskListRepository;
import com.taskmanager.respository.TaskRepository;

import java.util.List;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private TaskListRepository taskListRepository;

    public List<Task> getAllTasks() {
        return (List<Task>) taskRepository.findAll();
    }

    public Task getTaskById(Long taskId) {
        return taskRepository.findById(taskId).orElse(null);
    }

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    public Task updateTask(Task task) {
        return taskRepository.save(task);
    }

    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
    }

    public List<TaskList> getAllTaskLists() {
        return (List<TaskList>) taskListRepository.findAll();
    }

    public TaskList getTaskListById(Long taskListId) {
        return taskListRepository.findById(taskListId).orElse(null);
    }

    public TaskList createTaskList(TaskList taskList) {
        return taskListRepository.save(taskList);
    }

    public Task createTaskInList(Long taskListId, Task task) {
        TaskList taskList = taskListRepository.findById(taskListId).orElse(null);
        if (taskList != null) {
            task.setTaskList(taskList);
            return taskRepository.save(task);
        }
        return null;
    }

    public Task updateTaskInList(Long taskListId, Task task) {
        TaskList taskList = taskListRepository.findById(taskListId).orElse(null);
        if (taskList != null) {
            task.setTaskList(taskList);
            return taskRepository.save(task);
        }
        return null;
    }

    public void deleteTaskInList(Long taskListId, Long taskId) {
        TaskList taskList = taskListRepository.findById(taskListId).orElse(null);
        if (taskList != null) {
            List<Task> tasks = taskList.getTasks();
            tasks.removeIf(task -> task.getId().equals(taskId));
            taskListRepository.save(taskList);
        }
    }
}
