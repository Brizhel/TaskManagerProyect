package com.taskmanager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taskmanager.entity.Task;
import com.taskmanager.entity.TaskList;
import com.taskmanager.entity.User;
import com.taskmanager.exception.NoTaskListsFoundException;
import com.taskmanager.exception.TaskListAlreadyExistsException;
import com.taskmanager.exception.TaskListNotFoundException;
import com.taskmanager.request.TaskListRequest;
import com.taskmanager.respository.TaskListRepository;
import com.taskmanager.util.UserUtil;

import java.util.List;
import java.util.Optional;

@Service
public class TaskListService {
	@Autowired
	private TaskListRepository taskListRepository;
	@Autowired
	private UserUtil userUtil;


	public List<Task> getTasks(String listName) {
		List<TaskList> taskLists = getAllUserTasksLists();
		Optional<TaskList> optionalTaskList = taskLists.stream().filter(taskList -> taskList.getName().equals(listName))
				.findFirst();

		return optionalTaskList.map(TaskList::getTasks)
				.orElseThrow(() -> new TaskListNotFoundException("Lista de tareas no encontrada para el usuario"));
	}

	public TaskList createTaskList(String listName, User user) {
		// Verificar si ya existe una lista de tareas con el mismo nombre para el
		// usuario
		if (taskListRepository.existsByNameAndUser(listName, user)) {
			throw new TaskListAlreadyExistsException(
					"Ya existe una lista de tareas con el nombre '" + listName + "' para este usuario.");
		}

		TaskList taskList = new TaskList();
		taskList.setName(listName);
		taskList.setUser(user);

		// Guardar la nueva lista de tareas
		return taskListRepository.save(taskList);
	}

	public void deleteTaskListByNameAndUser(String listName, User user) {
		TaskList tasklist = taskListRepository.findByNameAndUser(listName, user);
		if (tasklist == null) {
			throw new TaskListNotFoundException(
					"No existe una lista de tareas con el nombre '" + listName + "' para este usuario.");
		}
		taskListRepository.delete(tasklist);
	}

	public TaskList updateTaskListNameForUser(User user, String oldName, String newName) {
		TaskList taskList = taskListRepository.findByNameAndUser(oldName, user);

		if (taskList == null) {
			throw new TaskListNotFoundException(
					"No se encontró la lista de tareas con el nombre proporcionado para el usuario.");
		}

		taskList.setName(newName);
		return taskListRepository.save(taskList);
	}

	public List<TaskList> searchTaskList(String name) {
		List<TaskList> taskLists = taskListRepository.findByNameContainingAndUser(name, userUtil.getUser());
		if (taskLists.isEmpty()) {
			throw new TaskListNotFoundException("No se encontró una lista de tareas con el nombre: " + name);
		}
		return taskLists;
	}

	public TaskList createTaskList(TaskListRequest taskList) {
		User user = userUtil.getUser();
		TaskList tasklist = createTaskList(taskList.getListName(), user);
		return tasklist;

	}

	public List<TaskList> getAllUserTasksLists() {
		User user = userUtil.getUser();
		List<TaskList> taskLists = user.getTaskLists();
		if (taskLists == null || taskLists.isEmpty()) {
			throw new NoTaskListsFoundException(user.getUsername());
		}
		return taskLists;
	}

	public TaskList updateTaskListName(String oldName, String newName) {
		return updateTaskListNameForUser(userUtil.getUser(), oldName, newName);
	}

	public void deleteTaskList(String taskList) {
		User user = userUtil.getUser();
		deleteTaskListByNameAndUser(taskList, user);
	}
}
