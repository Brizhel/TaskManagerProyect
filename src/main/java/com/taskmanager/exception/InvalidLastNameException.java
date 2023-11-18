package com.taskmanager.exception;


public class InvalidLastNameException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -6578381008646615380L;

	public InvalidLastNameException(String message) {
        super(message);
    }
}