package com.github.gcorporationcare.notest.common;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;

public class DataProviderTestHelper {

	protected static ValidatorFactory validatorFactory;
	protected static Validator validator;

	public static <T> T randomItem(List<T> items) {
		Random random = new Random();
		int index = random.nextInt(items.size());
		return items.get(index);
	}

	// -----------------------------------------------------
	// Embedded objects
	// -----------------------------------------------------

	@BeforeAll
	public static void setUpClass() {
		validatorFactory = Validation.buildDefaultValidatorFactory();
		validator = validatorFactory.getValidator();
	}

	protected void validateConstraint(Object obj, int expectedViolations, String[] expectedMessages,
			Class<?>... groups) {
		Set<ConstraintViolation<Object>> violations = Objects.isNull(groups) ? validator.validate(obj)
				: validator.validate(obj, groups);
		assertEquals(expectedViolations, violations.size());
		if (expectedViolations <= 0 || Objects.isNull(expectedMessages)) {
			return;
		}
		String[] messages = violations.stream().map(ConstraintViolation::getMessage).toArray(String[]::new);

		assertEquals(expectedMessages.length, messages.length);
		assertThat(Arrays.asList(messages), containsInAnyOrder(expectedMessages));
	}
}
