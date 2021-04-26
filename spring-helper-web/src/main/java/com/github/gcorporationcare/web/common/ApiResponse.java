package com.github.gcorporationcare.web.common;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.github.gcorporationcare.data.exception.ValidationException;
import com.github.gcorporationcare.data.i18n.I18nMessage;
import com.github.gcorporationcare.web.exception.RequestException;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Generic ApiResponse to send from API
 * 
 * @param <T> the type of the body contained in the response
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
public class ApiResponse<T> extends ResponseEntity<T> {

	public static final String DEFAULT_RESPONSE_ROOT = "content";
	public static final String TYPE_FIELD = "type";
	public static final String CODE_FIELD = "code";
	public static final String MESSAGE_FIELD = "message";
	public static final String TIMESTAMP_FIELD = "timestamp";
	public static final String STATUS_FIELD = "status";
	public static final String ERROR_FIELD = "error";
	public static final String REQUEST_ID_FIELD = "requestId";

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

	/**
	 * Convert a given Exception to a Map
	 * 
	 * @param e          the exception to convert
	 * @param statusCode the status code to send to API
	 * @param objects    the list of objects to use in translation
	 * @return a Map with keys (code, message, time stamp, etc...)
	 */
	public static Map<String, Object> exceptionToMap(Exception e, HttpStatus statusCode, Object... objects) {
		I18nMessage i18nMessage = I18nMessage.getInstance();

		Map<String, Object> map = new HashMap<>();
		if (!log.isDebugEnabled()) {
			map.put(TYPE_FIELD, e.getClass().getName());
		}
		map.put(CODE_FIELD, e.getMessage());
		map.put(MESSAGE_FIELD, i18nMessage.getMessage(e.getMessage(), objects));
		map.put(REQUEST_ID_FIELD, MDC.get(RequestIdGenerator.REQUEST_ID_MDC_KEY));
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
}
