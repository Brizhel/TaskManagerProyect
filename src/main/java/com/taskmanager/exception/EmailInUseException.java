package com.taskmanager.exception;

public class EmailInUseException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = -9034480067994386880L;

	public EmailInUseException(String email) {
        super("El correo electrónico '" + email + "' ya está en uso.");
    }
}
