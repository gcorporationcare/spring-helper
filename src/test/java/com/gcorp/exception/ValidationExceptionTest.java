package com.gcorp.exception;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gcorp.notest.DataProviderTestHelper;

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
		Assert.assertEquals(3, violations.size()); // (NotNull + NotEmpty + Min
		ValidationException e = new ValidationException(invalid.getName(), violations);
		Assert.assertEquals(String.format("Expected %s but go %s", invalid.getName(), e.getMessage()),
				invalid.getName(), e.getMessage());
		ObjectMapper mapper = new ObjectMapper();
		Assert.assertNotNull(mapper.writeValueAsString(e.toMap()));
	}

	@Test
	public void testToMap() {
		Assert.assertFalse((new ValidationException()).toMap().isEmpty());
		Assert.assertFalse((new ValidationException("With message")).toMap().isEmpty());
		Assert.assertNotNull(new ValidationException(new IOException(), null));
		Assert.assertNotNull(new ValidationException("Working", new IOException(), null));
	}
}
