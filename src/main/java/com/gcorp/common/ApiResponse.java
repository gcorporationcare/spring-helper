package com.gcorp.common;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.gcorp.exception.RequestException;
import com.gcorp.exception.ValidationException;
import com.gcorp.i18n.I18nMessage;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApiResponse<T> extends ResponseEntity<T> {

	public static final String DEFAULT_RESPONSE_ROOT = "content";
	public static final String TYPE_FIELD = "type";
	public static final String CODE_FIELD = "code";
	public static final String MESSAGE_FIELD = "message";
	public static final String TIMESTAMP_FIELD = "timestamp";
	public static final String STATUS_FIELD = "status";
	public static final String ERROR_FIELD = "error";

	@Getter
	private final boolean success;

	public ApiResponse(HttpStatus statusCode) {
		super(statusCode);
		success = isSuccessStatusCode(statusCode);
	}

	public ApiResponse(T body, HttpStatus statusCode) {
		super(body, statusCode);
		success = isSuccessStatusCode(statusCode);
	}

	@SuppressWarnings({ "unchecked" })
	public ApiResponse(Exception e, HttpStatus statusCode, Object... objects) {
		super((T) exceptionToMap(e, statusCode, objects), statusCode);
		success = isSuccessStatusCode(statusCode);
	}

	public ApiResponse(RequestException e) {
		this(e, e.getStatusCode());
	}

	@SuppressWarnings("unchecked")
	public ApiResponse(ValidationException e) {
		super((T) e.toMap(), HttpStatus.BAD_REQUEST);
		success = false;
	}

	public static Map<String, Object> exceptionToMap(Exception e, HttpStatus statusCode, Object... objects) {
		I18nMessage apiMessage = I18nMessage.getInstance();

		Map<String, Object> map = new HashMap<>();
		if (!log.isDebugEnabled()) {
			map.put(TYPE_FIELD, e.getClass().getName());
		}
		map.put(CODE_FIELD, e.getMessage());
		map.put(MESSAGE_FIELD, apiMessage.getMessage(e.getMessage(), objects));
		map.put(TIMESTAMP_FIELD, LocalDateTime.now().toString());
		if (statusCode != null) {
			map.put(STATUS_FIELD, String.valueOf(statusCode.value()));
			map.put(ERROR_FIELD, statusCode.getReasonPhrase());
		}
		return map;
	}

	private boolean isSuccessStatusCode(HttpStatus statusCode) {
		return !statusCode.is4xxClientError() && !statusCode.is5xxServerError();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof ApiResponse))
			return false;
		@SuppressWarnings("unchecked")
		ResponseEntity<T> o = (ResponseEntity<T>) other;
		return super.equals(o);
	}

	@Override
	public int hashCode() {
		return this.getStatusCode().hashCode() * 17 + super.hashCode();
	}
}
