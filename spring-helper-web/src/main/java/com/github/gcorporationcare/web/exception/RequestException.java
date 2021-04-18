package com.github.gcorporationcare.web.exception;

import java.io.Serializable;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class RequestException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public final HttpStatus statusCode;
	public final Serializable[] details;

	public RequestException(String message) {
		super(message);
		statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
		details = null;
	}

	public RequestException(String message, HttpStatus statusCode) {
		super(message);
		this.statusCode = statusCode;
		details = null;
	}

	public RequestException(String message, HttpStatus statusCode, Serializable... details) {
		super(message);
		this.statusCode = statusCode;
		this.details = details;
	}
}
