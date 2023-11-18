package com.taskmanager.exception;

public class NoTaskListsFoundException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -2146192883866326202L;

	public NoTaskListsFoundException(String username) {
        super("No se encontraron listas de tareas para el usuario: " + username);
    }
}