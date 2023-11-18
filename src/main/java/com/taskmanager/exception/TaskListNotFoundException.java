package com.taskmanager.exception;

public class TaskListNotFoundException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 3462893476034824340L;

	public TaskListNotFoundException(String message) {
        super(message);
    }

    public TaskListNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}