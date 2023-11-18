package com.taskmanager.exception;

public class PasswordNotChangedException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 7285958062877126747L;

	public PasswordNotChangedException(String message) {
        super(message);
    }
}