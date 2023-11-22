package com.taskmanager.exception;

public class RoleNotFoundException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -6145813913112130003L;

	public RoleNotFoundException() {
        super();
    }

    public RoleNotFoundException(String message) {
        super(message);
    }

    public RoleNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}