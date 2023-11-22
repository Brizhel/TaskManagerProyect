package com.taskmanager.exception;

public class WeakPasswordException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 5175572366386821043L;

	public WeakPasswordException() {
        super("La contraseña es débil.");
    }

    public WeakPasswordException(String message) {
        super(message);
    }
}