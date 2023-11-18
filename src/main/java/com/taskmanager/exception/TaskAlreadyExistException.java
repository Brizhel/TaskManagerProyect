package com.taskmanager.exception;

public class TaskAlreadyExistException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 2698374622822015429L;

	public TaskAlreadyExistException(String message) {
        super(message);
    }

    public TaskAlreadyExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
