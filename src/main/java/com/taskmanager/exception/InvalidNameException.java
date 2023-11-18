package com.taskmanager.exception;

public class InvalidNameException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -258824760473648506L;

	public InvalidNameException(String message) {
        super(message);
    }
}