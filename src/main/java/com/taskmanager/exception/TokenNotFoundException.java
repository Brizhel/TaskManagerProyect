package com.taskmanager.exception;

public class TokenNotFoundException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 7546391164784912685L;

	public TokenNotFoundException(String message) {
        super(message);
    }

    public TokenNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}