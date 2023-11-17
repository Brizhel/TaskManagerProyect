package com.taskmanager.exception;

public class UsernameTakenException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = -1216646864789235854L;

	public UsernameTakenException() {
        super("El nombre de usuario ya está en uso.");
    }

    public UsernameTakenException(String message) {
        super(message);
    }

    public UsernameTakenException(String message, Throwable cause) {
        super(message, cause);
    }
}

