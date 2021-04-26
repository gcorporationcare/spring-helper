package com.github.gcorporationcare.web.interceptor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.github.gcorporationcare.data.i18n.I18nMessage;
import com.github.gcorporationcare.web.common.ApiResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class BaseValidationInterceptor extends ResponseEntityExceptionHandler {
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		log.error("Error occured because of invalid argument {}", e);
		I18nMessage i18nMessage = I18nMessage.getInstance();
		BindingResult bindingResult = e.getBindingResult();
		List<String> messages = new ArrayList<>();
		bindingResult.getFieldErrors().stream().forEach(error -> {
			String message = i18nMessage.getMessage(error.getDefaultMessage(), error.getField(),
					error.getRejectedValue());
			if (!messages.contains(message))
				messages.add(message);
		});
		Exception exception = new Exception(String.join(",", messages.toArray(new String[] {})));
		return new ApiResponse<>(ApiResponse.exceptionToMap(exception, HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
	}

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException e,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		log.error("Error occured unreadable message {}", e);
		return new ApiResponse<>(e, HttpStatus.BAD_REQUEST, e.getMessage());
	}
}
