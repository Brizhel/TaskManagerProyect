package com.taskmanager.exception;

public class TaskNotFoundException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 9135219245762315381L;

	public TaskNotFoundException(String message) {
        super(message);
    }

    public TaskNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}