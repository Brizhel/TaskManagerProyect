package com.taskmanager.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.taskmanager.entity.Task;
import com.taskmanager.exception.TaskAlreadyExistException;
import com.taskmanager.exception.TaskListNotFoundException;
import com.taskmanager.exception.TaskNotFoundException;
import com.taskmanager.exception.UserNotFoundException;
import com.taskmanager.request.TaskRequest;
import com.taskmanager.service.TaskListService;
import com.taskmanager.service.TaskService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/user/task-lists/{listName}/tasks")
public class TaskController {
	
	@Autowired
	private TaskListService taskListService;
	@Autowired
	private TaskService taskService;
    
    @GetMapping
    public ResponseEntity<?> getTasks(
    		@PathVariable String listName) {
        try {
            List<Task> tasks = taskListService.getTasks(listName);
            return ResponseEntity.ok(tasks);
        } catch (TaskListNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lista de tareas inválida");
        }
    }
	@PostMapping
	public ResponseEntity<?> createTask(
			@PathVariable String listName ,
			@Valid @RequestBody TaskRequest taskRequest) {
	    try {
	        Task createdTask = taskService.createTask(taskRequest,listName);
	        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
	    } catch (UserNotFoundException | TaskListNotFoundException e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
	    } catch (TaskAlreadyExistException e) {
	    	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
	    }
	    catch (Exception e) {
	        return ResponseEntity.internalServerError().body("Error Interno");
	    }
	}
    @DeleteMapping("/{taskName}")
    public ResponseEntity<?> deleteTask(
    		@PathVariable String listName, 
    		@PathVariable String taskName) {
        try {
            taskService.deleteTask(listName, taskName);
            return ResponseEntity.noContent().build();
        } catch (UserNotFoundException | TaskListNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        } catch (TaskNotFoundException e) {
        	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error Interno");
        }
    }
    @PatchMapping("/{taskName}")
    public ResponseEntity<?> updateTask(
    		@PathVariable String listName, 
    		@PathVariable String taskName,
            @RequestBody TaskRequest taskRequest) {
        try {
            Task updatedTask = taskService.updateTask(listName, taskName, taskRequest);
            return ResponseEntity.ok(updatedTask);
        } catch (UserNotFoundException | TaskListNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error Interno");
        }
    }
    @PatchMapping("/complete/{taskName}")
    public ResponseEntity<?> markTaskAsCompleted(
    		@PathVariable String listName,
    		@PathVariable String taskName) {
        try {
            Task completedTask = taskService.markTaskAsCompleted(listName, taskName);
            return ResponseEntity.ok(completedTask);
        } catch (UserNotFoundException | TaskListNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        } catch (TaskNotFoundException e) {
        	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
        	catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error Interno");
        }
    }
    @GetMapping("/search")
    public ResponseEntity<?> searchTaskB(
    		@PathVariable String listName, 
    		@RequestParam(required = false) String keyword, 
    		@RequestParam(required = false) boolean completion){
    	try {
    		List<Task> tasks = taskService.searchTaskByKeywordAndCompleted(keyword, completion, listName);
    		return new ResponseEntity<>(tasks, HttpStatus.OK);
    	} catch (TaskNotFoundException e) {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}
    }
}
