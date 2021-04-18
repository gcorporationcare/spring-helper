package com.github.gcorporationcare.data.exception;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;

import com.github.gcorporationcare.data.common.ApiFieldError;

import lombok.Getter;

/**
 * Suited for exception concerning validation of fields, properties,
 * situations...
 */
@Getter
public class ValidationException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private final ApiFieldError[] violations;

	public ValidationException() {
		super();
		violations = getViolationsAsApiFieldError(null);
	}

	public ValidationException(String message) {
		super(message);
		violations = getViolationsAsApiFieldError(null);
	}

	public ValidationException(String message, Set<ConstraintViolation<Object>> violations) {
		super(message);
		this.violations = getViolationsAsApiFieldError(violations);
	}

	public ValidationException(Throwable cause, Set<ConstraintViolation<Object>> violations) {
		super(cause);
		this.violations = getViolationsAsApiFieldError(violations);
	}

	public ValidationException(String message, Throwable cause, Set<ConstraintViolation<Object>> violations) {
		super(message, cause);
		this.violations = getViolationsAsApiFieldError(violations);
	}

	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("message", getMessage());
		map.put("violations", violations);
		return map;
	}

	private ApiFieldError[] getViolationsAsApiFieldError(Set<ConstraintViolation<Object>> violations) {
		if (violations == null)
			return new ApiFieldError[0];
		return violations.stream()
				.map(f -> new ApiFieldError(f.getPropertyPath().toString(), f.getMessage(),
						(Serializable) f.getInvalidValue()))
				.collect(Collectors.toList()).toArray(new ApiFieldError[] {});
	}
}
