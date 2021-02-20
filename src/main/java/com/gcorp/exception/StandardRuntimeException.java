package com.gcorp.exception;

/**
 * Project's specific runtime exception
 * */
public class StandardRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public StandardRuntimeException() {
		super();
	}

	public StandardRuntimeException(String message) {
		 super(message);
	}

	public StandardRuntimeException(Throwable cause) {
		 super(cause);
	}

	public StandardRuntimeException(String message, Throwable cause) {
		 super(message, cause);
	}
}
