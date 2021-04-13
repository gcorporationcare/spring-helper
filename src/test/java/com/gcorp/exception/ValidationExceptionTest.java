package com.gcorp.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gcorp.common.ApiFieldError;
import com.gcorp.notest.common.DataProviderTestHelper;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ValidationExceptionTest extends DataProviderTestHelper {
	@Data
	@NoArgsConstructor
	public class SimpleObject {
		@Getter
		LocalDateTime date;
		@Getter
		@NotNull
		@NotEmpty
		String name;
		@Getter
		@Min(1)
		int number;

		SimpleObject(String name, int number) {
			this.date = LocalDateTime.now();
			this.name = name;
			this.number = number;
		}
	}

	final static int MAX_LENGTH = 100;

	@Test
	public void testSerialization() throws JsonProcessingException {
		SimpleObject invalid = new SimpleObject();
		Set<ConstraintViolation<Object>> violations = validator.validate(invalid);
		assertEquals(3, violations.size()); // (NotNull + NotEmpty + Min)
		ValidationException e = new ValidationException("Simple error message", violations);
		ApiFieldError[] errors = e.getViolations();
		Arrays.stream(errors).forEach(error -> {
			assertNotNull(error.getCode());
			assertNotNull(error.getField());
		});
		ObjectMapper mapper = new ObjectMapper();
		assertNotNull(mapper.writeValueAsString(e.toMap()));
	}

	@Test
	public void testToMap() {
		assertFalse((new ValidationException()).toMap().isEmpty());
		assertFalse((new ValidationException("With message")).toMap().isEmpty());
		assertNotNull(new ValidationException(new IOException(), null));
		assertNotNull(new ValidationException("Working", new IOException(), null));
	}
}
