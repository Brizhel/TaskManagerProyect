package com.taskmanager.response;

import java.time.LocalDate;

public class TaskResponse {
	private String name;
	private String description;
	private LocalDate dueDate;
	private boolean completed;
	public TaskResponse(String name, String description, LocalDate dueDate, boolean completed) {
		this.name = name;
		this.description = description;
		this.dueDate = dueDate;
		this.completed = completed;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public LocalDate getDueDate() {
		return dueDate;
	}
	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}
	public boolean isCompleted() {
		return completed;
	}
	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
}
