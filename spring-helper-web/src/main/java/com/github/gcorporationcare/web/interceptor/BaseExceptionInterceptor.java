package com.github.gcorporationcare.web.interceptor;

import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.gcorporationcare.data.exception.StandardRuntimeException;
import com.github.gcorporationcare.data.exception.ValidationException;
import com.github.gcorporationcare.data.i18n.I18nMessage;
import com.github.gcorporationcare.web.common.ApiResponse;
import com.github.gcorporationcare.web.exception.RequestException;

import lombok.extern.slf4j.Slf4j;

/**
 * Intercept all exception and translate them to a API response
 */
@Slf4j
@ControllerAdvice
public class BaseExceptionInterceptor {
	protected static final Object[] EMPTY_OBJECTS = new Object[0];

	@ResponseBody
	@org.springframework.web.bind.annotation.ExceptionHandler(AuthenticationException.class)
	public ApiResponse<Map<String, String>> authenticationException(AuthenticationException e) {
		log.error("Error occured because of authentication exception", e);
		return new ApiResponse<>(e, HttpStatus.UNAUTHORIZED, EMPTY_OBJECTS);
	}

	/**
	 * Catch usual access denied exceptions
	 * 
	 * @param e {@link AccessDeniedException}
	 * @return {@link ApiResponse}
	 */
	@ResponseBody
	@org.springframework.web.bind.annotation.ExceptionHandler({ AccessDeniedException.class })
	public ApiResponse<Map<String, Object>> accessDeniedException(AccessDeniedException e) {
		log.error("Error occured because of access denied exception {}", e);
		return new ApiResponse<>(e, HttpStatus.FORBIDDEN, EMPTY_OBJECTS);
	}

	/**
	 * Catch any technical exception and return an HTTP response.
	 *
	 * @param e {@link RequestException}
	 * @return {@link ApiResponse}
	 */
	@ResponseBody
	@org.springframework.web.bind.annotation.ExceptionHandler(RequestException.class)
	public ApiResponse<Map<String, String>> requestException(RequestException e) {
		log.error("Error occured because of request exception {}", e);
		return new ApiResponse<>(e, e.getStatusCode(), (Object[]) e.getDetails());
	}

	@ResponseBody
	@org.springframework.web.bind.annotation.ExceptionHandler({ InvalidDataAccessApiUsageException.class })
	public ApiResponse<Map<String, String>> invalidDataAccessApiUsageException(InvalidDataAccessApiUsageException e) {
		log.error("Error occured because of invalid data access exception {}", e);
		return new ApiResponse<>(e, HttpStatus.BAD_REQUEST, EMPTY_OBJECTS);
	}

	@ResponseBody
	@org.springframework.web.bind.annotation.ExceptionHandler({ DateTimeParseException.class })
	public ApiResponse<Map<String, String>> dateTimeParseException(DateTimeParseException e) {
		log.error("Error occured because of date time parse exception {}", e);
		return new ApiResponse<>(e, HttpStatus.BAD_REQUEST, EMPTY_OBJECTS);
	}

	/**
	 * Catch any data integrity exception and return an HTTP response.
	 *
	 * @param e {@link DataIntegrityViolationException}
	 * @return {@link ApiResponse}
	 */
	@ResponseBody
	@org.springframework.web.bind.annotation.ExceptionHandler(DataIntegrityViolationException.class)
	public ApiResponse<Map<String, String>> dataIntegrityViolation(DataIntegrityViolationException e) {
		log.error("Error occured because of data integrity violation exception {}", e);
		return new ApiResponse<>(e, HttpStatus.BAD_REQUEST, EMPTY_OBJECTS);
	}

	/**
	 * Catch any validation exception and return an HTTP response.
	 *
	 * @param e {@link RequestException}
	 * @return {@link ApiResponse}
	 */
	@ResponseBody
	@org.springframework.web.bind.annotation.ExceptionHandler(ValidationException.class)
	public ApiResponse<Map<String, Object>> validationException(ValidationException e) {
		log.error("Error occured because of validation exception {}", e);
		I18nMessage apiMessage = I18nMessage.getInstance();
		List<String> messages = new ArrayList<>();
		Arrays.stream(e.getViolations()).forEach(v -> {
			String message = apiMessage.getMessage(v.getCode(), v.getRejectedValue(), v.getField());
			if (!messages.contains(message))
				messages.add(message);
		});
		StringBuilder sb = new StringBuilder(apiMessage.getMessage(e.getMessage(), e.getViolations().length) + ": ");
		sb.append(String.join(",", messages.toArray(new String[] {})));
		Exception exception = new Exception(sb.toString());
		return new ApiResponse<>(ApiResponse.exceptionToMap(exception, HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
	}

	/**
	 * Catch usual Runtime exceptions
	 * 
	 * @param e {@link RuntimeException}
	 * @return {@link ApiResponse}
	 */
	@ResponseBody
	@org.springframework.web.bind.annotation.ExceptionHandler({ NullPointerException.class,
			IllegalArgumentException.class, StandardRuntimeException.class, HttpMessageNotWritableException.class })
	public ApiResponse<Map<String, Object>> runtimeException(RuntimeException e) {
		log.error("Error occured because of runtime exception {}", e);
		return new ApiResponse<>(e, HttpStatus.BAD_REQUEST, EMPTY_OBJECTS);
	}
}
