package com.taskmanager.exception;

public class EmptyKeywordException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 7695555088279892230L;

	public EmptyKeywordException(String message) {
        super(message);
    }
}
