package com.taskmanager.exception;

public class TaskListAlreadyExistsException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -2869168860699865034L;

	public TaskListAlreadyExistsException(String message) {
        super(message);
    }
}