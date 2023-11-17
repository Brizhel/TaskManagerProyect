package com.taskmanager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taskmanager.entity.Task;
import com.taskmanager.entity.TaskList;
import com.taskmanager.respository.TaskListRepository;
import com.taskmanager.respository.TaskRepository;

import jakarta.persistence.EntityNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TaskListService {
	@Autowired
	private TaskRepository taskRepository;
    @Autowired
    private TaskListRepository taskListRepository;

    public List<TaskList> getAllTaskLists() {
        return (List<TaskList>) taskListRepository.findAll();
    }

    public TaskList getTaskListById(Long taskListId) {
        return taskListRepository.findById(taskListId).orElse(null);
    }

    public List<Task> getTasksInList(Long taskListId) {
        TaskList taskList = taskListRepository.findById(taskListId).orElse(null);
        if (taskList != null) {
            return taskList.getTasks();
        }
        return null;
    }

    public TaskList createTaskList(TaskList taskList) {
        // Verificar si ya existe una lista de tareas con el mismo nombre
        TaskList existingTaskList = taskListRepository.findByName(taskList.getName());
        
        if (existingTaskList != null) {
            // Una lista con el mismo nombre ya existe, no la crees nuevamente
            return existingTaskList;
        } else {
            // La lista de tareas no existe, así que la creamos
            return taskListRepository.save(taskList);
        }
    }

    public TaskList updateTaskList(TaskList taskList) {
        return taskListRepository.save(taskList);
    }

    public void deleteTaskList(Long taskListId) {
        taskListRepository.deleteById(taskListId);
    }

    public Task createTaskInList(Long taskListId, Task task) {
        TaskList taskList = taskListRepository.findById(taskListId).orElse(null);
        if (taskList != null) {
            // Verificar si una tarea similar ya existe en la lista
            List<Task> tasks = taskList.getTasks();
            for (Task existingTask : tasks) {
                if (existingTask.getName().equals(task.getName()) && existingTask.getDescription().equals(task.getDescription())) {
                    // La tarea ya existe, no la crees nuevamente
                    return existingTask;
                }
            }
            
            // La tarea no existe, así que la asociamos y la creamos
            task.setTaskList(taskList);
            return taskRepository.save(task);
        }
        return null;
    }
    public void deleteTaskInList(Long taskListId, Long taskId) {
        Optional<TaskList> taskListOptional = taskListRepository.findById(taskListId);
        if (taskListOptional.isPresent()) {
            TaskList taskList = taskListOptional.get();
            
            // Obtiene la tarea a eliminar
            Optional<Task> taskToRemove = taskList.getTasks().stream()
                .filter(task -> task.getId().equals(taskId))
                .findFirst();
            
            if (taskToRemove.isPresent()) {
                Task task = taskToRemove.get();
                
                // Actualiza la relación bidireccional
                task.setTaskList(null); // Limpia la referencia en la entidad Task
                
                // Remueve la tarea de la lista
                taskList.getTasks().remove(task);
                
                // Guarda los cambios
                taskRepository.delete(task); // O elimina la tarea desde el repositorio de tareas
                taskListRepository.save(taskList);
            } else {
                // Manejo si la tarea no se encontró en la lista de tareas.
            }
        } else {
            // Manejo si la lista de tareas no se encontró.
        }
    }

    public List<Task> searchTasksInList(Long taskListId, String keyword) {
        TaskList taskList = taskListRepository.findById(taskListId).orElse(null);

        if (taskList != null) {
            List<Task> tasks = taskList.getTasks();
            List<Task> matchingTasks = new ArrayList<>();

            // Realiza la búsqueda y filtrado por nombre de tarea
            for (Task task : tasks) {
                if (task.getName().toLowerCase().contains(keyword.toLowerCase())) {
                    matchingTasks.add(task);
                }
            }

            return matchingTasks;
        } else {
            // Manejo si la lista de tareas no se encuentra
            throw new EntityNotFoundException("La lista de tareas no se encontró");
        }
    }
    public Task markTaskAsCompleted(Long taskListId, Long taskId) {
        TaskList taskList = taskListRepository.findById(taskListId).orElse(null);

        if (taskList != null) {
            List<Task> tasks = taskList.getTasks();

            for (Task task : tasks) {
                if (task.getId().equals(taskId)) {
                    task.setCompleted(!task.isCompleted()); // Invierte el estado de completado
                    taskRepository.save(task); // Guarda el cambio en la base de datos
                    return task;
                }
            }

            // Manejo si la tarea no se encuentra en la lista de tareas.
            throw new EntityNotFoundException("La tarea no se encontró en la lista de tareas.");
        } else {
            // Manejo si la lista de tareas no se encuentra.
            throw new EntityNotFoundException("La lista de tareas no se encontró.");
        }
    }

    public List<Task> sortTasksInList(Long taskListId, String orderBy, String direction) {
        TaskList taskList = taskListRepository.findById(taskListId).orElse(null);

        if (taskList != null) {
            List<Task> tasks = taskList.getTasks();

            // Implementa la lógica de ordenación por fecha de vencimiento
            tasks.sort((task1, task2) -> {
                int result;
                if ("dueDate".equals(orderBy)) {
                    result = task1.getDueDate().compareTo(task2.getDueDate());
                } else {
                    // Ordenar por defecto por ID
                    result = task1.getId().compareTo(task2.getId());
                }
                return "asc".equals(direction) ? result : -result;
            });

            return tasks;
        } else {
            // Manejo si la lista de tareas no se encuentra.
            throw new EntityNotFoundException("La lista de tareas no se encontró.");
        }
    }
}
