package com.taskmanager.exception;

public class PasswordMismatchException extends RuntimeException{
    /**
	 * 
	 */
	private static final long serialVersionUID = 8490309898186649844L;

	public PasswordMismatchException(String message) {
        super(message);
    }
}
