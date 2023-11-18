package com.taskmanager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taskmanager.entity.Task;
import com.taskmanager.entity.TaskList;
import com.taskmanager.exception.TaskAlreadyExistException;
import com.taskmanager.exception.TaskNotFoundException;
import com.taskmanager.request.TaskRequest;
import com.taskmanager.respository.TaskListRepository;
import com.taskmanager.respository.TaskRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private TaskListRepository taskListRepository;
    @Autowired
    private UserService userService;
    public Task getTask(String taskName, String taskList) {
    	Task task = taskRepository.findByNameAndTaskList(taskName, getListTaskByNameAndUser(taskList));
    	if (task == null) {
    		throw new TaskNotFoundException("Tarea no encontrada");
    	}
    	return task;
    }
    public Task createTask(TaskRequest request, String taskListName) {
    	Task taskTry = getTask(request.getName(), taskListName);
    	if (taskTry != null) {
    		throw new TaskAlreadyExistException("La tarea ya existe");
    	}
    	Task task = new Task();
    	task.setName(request.getName());
    	task.setDescription(request.getDescription());
    	task.setDueDate(request.getDueDate());
    	task.setTaskList(getListTaskByNameAndUser(taskListName));
    	return taskRepository.save(task);
    }
    public void deleteTask(String taskName, String taskList) {
    	Task task = getTask(taskName, taskList);
    	if (task == null) {
    		throw new TaskNotFoundException("Tarea no encontrada");
    	}
    	taskRepository.delete(task);
    }
    public void updateTaskName(String oldTaskName, String newTaskName, String tasklist) {
    	Task task = getTask(oldTaskName, tasklist);
    	task.setName(newTaskName);
    	taskRepository.save(task);
    }
    public void updateTaskDescription(String taskName,String newDescription, String taskList) {
    	Task task = getTask(taskName,taskList);
    	task.setDescription(newDescription);
    	taskRepository.save(task);
    }
    public void updateTaskDueDate(String taskName, LocalDate newDueDate, String taskList) {
    	Task task = getTask(taskName, taskList);
    	task.setDueDate(newDueDate);
    	taskRepository.save(task);
    }
    public void setCompleted(String taskName,boolean completed, String taskList) {
    	Task task = getTask(taskName, taskList);
    	task.setCompleted(completed);
    }
    public TaskList getListTaskByNameAndUser(String TaskListName) {
    	TaskList taskList = taskListRepository.findByNameAndUser(TaskListName, userService.getUser());
    	return taskList;
    }
	public Task updateTask(String listName, String taskName, TaskRequest taskRequest) {
		Task task = getTask(taskName, listName);
		if (task == null) {
			throw new TaskNotFoundException("Tarea no encontrada");
		}
		task.setName(taskRequest.getName());
		task.setDescription(taskRequest.getDescription());
		task.setDueDate(taskRequest.getDueDate());
		return taskRepository.save(task);
	}
	public Task markTaskAsCompleted(String listName, String taskName) {
		Task task = getTask(taskName, listName);
		task.setCompleted(!task.isCompleted());
		return taskRepository.save(task);
	}
	public List<Task> searchTaskContainName(String keyword, String taskListName) {
		TaskList tasklist = getListTaskByNameAndUser(taskListName);
		List<Task> tasks = taskRepository.findByNameContainingAndTaskList(keyword, tasklist);
		if (tasks.isEmpty()) {
			throw new TaskNotFoundException("No se encontraron tareas con la palabra clave: " + keyword);
		}
		return tasks;
	}
	public List<Task> searchTaskByKeywordAndCompleted(String keyword,boolean completed, String taskListName){
			TaskList tasklist = getListTaskByNameAndUser(taskListName);
			return taskRepository.findByNameContainingAndCompletedAndTaskList(keyword, completed, tasklist);
	}
}
