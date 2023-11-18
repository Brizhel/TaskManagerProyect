package com.taskmanager.exception;

public class TokenExpiredException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 4079318972766577477L;

	public TokenExpiredException(String message) {
        super(message);
    }

    public TokenExpiredException(String message, Throwable cause) {
        super(message, cause);
    }
}

