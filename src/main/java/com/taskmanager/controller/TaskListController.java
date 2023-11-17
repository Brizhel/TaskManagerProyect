package com.taskmanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.taskmanager.entity.Task;
import com.taskmanager.entity.TaskList;
import com.taskmanager.service.TaskListService;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/task-lists")
public class TaskListController {
    @Autowired
    private TaskListService taskListService;

    @GetMapping
    public List<TaskList> getAllTaskLists() {
        return taskListService.getAllTaskLists();
    }

    @GetMapping("/{taskListId}")
    public TaskList getTaskList(@PathVariable Long taskListId) {
        return taskListService.getTaskListById(taskListId);
    }

    @GetMapping("/{taskListId}/tasks")
    public List<Task> getTasksInList(@PathVariable Long taskListId) {
        return taskListService.getTasksInList(taskListId);
    }

    @PostMapping
    public TaskList createTaskList(@Valid @RequestBody TaskList taskList) {
        return taskListService.createTaskList(taskList);
    }

    @PutMapping("/{taskListId}")
    public TaskList updateTaskList(@Valid @PathVariable Long taskListId, @RequestBody TaskList taskList) {
        taskList.setId(taskListId);
        return taskListService.updateTaskList(taskList);
    }

    @DeleteMapping("/{taskListId}")
    public void deleteTaskList(@PathVariable Long taskListId) {
        taskListService.deleteTaskList(taskListId);
    }

    @PostMapping("/{taskListId}/tasks")
    public Task createTaskInList(@PathVariable Long taskListId, @Valid @RequestBody Task task) {
        return taskListService.createTaskInList(taskListId, task);
    }

    @DeleteMapping("/{taskListId}/tasks/{taskId}")
    public void deleteTaskInList(@PathVariable Long taskListId, @PathVariable Long taskId) {
        taskListService.deleteTaskInList(taskListId, taskId);
    }
    @GetMapping("/{taskListId}/tasks/search")
    public List<Task> searchTasksInList(@PathVariable Long taskListId, @RequestParam("keyword") String keyword) {
        // Aquí puedes implementar la lógica de búsqueda y filtrado.
        return taskListService.searchTasksInList(taskListId, keyword);
    }
    @PatchMapping("/{taskListId}/tasks/{taskId}/complete")
    public Task markTaskAsCompleted(@PathVariable Long taskListId, @PathVariable Long taskId) {
        return taskListService.markTaskAsCompleted(taskListId, taskId);
    }
    @GetMapping("/{taskListId}/tasks/sort")
    public List<Task> sortTasksInList(
            @PathVariable Long taskListId,
            @RequestParam(name = "orderBy", defaultValue = "id") String orderBy,
            @RequestParam(name = "direction", defaultValue = "asc") String direction) {
        return taskListService.sortTasksInList(taskListId, orderBy, direction);
    }
}
