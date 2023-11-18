package com.taskmanager.controller;

import java.util.List;
import java.util.stream.Collectors;

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

import com.taskmanager.entity.TaskList;
import com.taskmanager.exception.NoTaskListsFoundException;
import com.taskmanager.exception.TaskListAlreadyExistsException;
import com.taskmanager.exception.TaskListNotFoundException;
import com.taskmanager.exception.UserNotFoundException;
import com.taskmanager.request.TaskListRequest;
import com.taskmanager.response.TaskListResponse;
import com.taskmanager.service.TaskListService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/user/task-lists")
public class TaskListController {
	@Autowired
	private TaskListService taskListService;

	@GetMapping
	public ResponseEntity<?> getAllTaskLists() {
		try {
			List<TaskListResponse> taskListResponses = taskListService.getAllUserTasksLists().stream()
					.map(taskList -> new TaskListResponse(taskList.getId(), taskList.getName()))
					.collect(Collectors.toList());
			return ResponseEntity.ok(taskListResponses);
		} catch (NoTaskListsFoundException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
		}
	}

	@PostMapping
	public ResponseEntity<?> createTaskList(@Valid @RequestBody TaskListRequest taskListRequest) {
		try {
			TaskList createdTaskList = taskListService.createTaskList(taskListRequest);
			return ResponseEntity.status(HttpStatus.CREATED).body(createdTaskList);
		} catch (UserNotFoundException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
		} catch (NoTaskListsFoundException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: No se encontraron listas de tareas");
		} catch (TaskListAlreadyExistsException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error " + e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body("Error Interno");
		}
	}

	@PatchMapping("/{taskListName}")
	public ResponseEntity<?> updateTaskList(@PathVariable String taskListName,
			@Valid @RequestBody TaskListRequest taskListRequest) {
		try {
			TaskList updatedTaskList = taskListService.updateTaskListName(taskListName, taskListRequest.getListName());
			return ResponseEntity.ok(updatedTaskList);
		} catch (TaskListNotFoundException | UserNotFoundException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body("Error Interno");
		}
	}

	@DeleteMapping("/{taskListName}")
	public ResponseEntity<?> deleteTaskList(@PathVariable String taskListName) {
		try {
			taskListService.deleteTaskList(taskListName);
			return ResponseEntity.noContent().build();
		} catch (TaskListNotFoundException | UserNotFoundException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body("Error Interno");
		}
	}

	@GetMapping("/search")
	public ResponseEntity<?> searchTaskList(@RequestParam String taskListName) {
		try {
			List<TaskList> taskLists = taskListService.searchTaskList(taskListName);
			return ResponseEntity.ok(taskLists);
		} catch (TaskListNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error Interno");
		}
	}
}
