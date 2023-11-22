package com.taskmanager.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
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

import com.taskmanager.RoleInitializer;
import com.taskmanager.entity.TaskList;
import com.taskmanager.exception.NoTaskListsFoundException;
import com.taskmanager.exception.TaskListAlreadyExistsException;
import com.taskmanager.exception.TaskListNotFoundException;
import com.taskmanager.exception.UserNotFoundException;
import com.taskmanager.request.TaskListRequest;
import com.taskmanager.response.TaskListResponse;
import com.taskmanager.service.TaskListService;
import com.taskmanager.util.UserUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/user/task-lists")
public class TaskListController {
	private static final Logger logger = LoggerFactory.getLogger(RoleInitializer.class);
    private static final Marker FILELOG = MarkerFactory.getMarker("FILELOG");
    @Autowired
    private UserUtil userUtil;
	@Autowired
	private TaskListService taskListService;
	@Autowired
	private ModelMapper modelMapper;
	@GetMapping
	public ResponseEntity<?> getAllTaskLists() {
	    try {
	        List<TaskListResponse> taskListResponses = taskListService.getAllUserTasksLists().stream()
	                .map(taskList -> modelMapper.map(taskList, TaskListResponse.class))
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
	        logger.info(FILELOG, "El usuario: " + userUtil.getUser().getUsername()+ "Ha creado una lista de tareas: " + createdTaskList.getName() );

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
	        logger.info(FILELOG, "El usuario: " + userUtil.getUser().getUsername()+ "ha modificado una lista de tareas: " + updatedTaskList.getName() );

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
	        logger.info(FILELOG, "El usuario: " + userUtil.getUser().getUsername()+ "ha borrado una lista de tareas: " + taskListName);

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
