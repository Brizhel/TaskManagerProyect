package com.taskmanager.exception;

public class InvalidPasswordException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = -63886693913932873L;

	public InvalidPasswordException() {
        super("La contraseña no es válida.");
    }

    public InvalidPasswordException(String message) {
        super(message);
    }

    public InvalidPasswordException(String message, Throwable cause) {
        super(message, cause);
    }
}
