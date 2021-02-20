package com.gcorp.notest;

import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
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

	@BeforeClass
	public static void setUpClass() {
		validatorFactory = Validation.buildDefaultValidatorFactory();
		validator = validatorFactory.getValidator();
	}

	protected void validateConstraint(Object obj, int expectedViolations, String exepctedMessage) {
		Set<ConstraintViolation<Object>> violations = validator.validate(obj);
		Assert.assertEquals(expectedViolations, violations.size());
		if (expectedViolations > 0)
			Assert.assertEquals(exepctedMessage, violations.toArray(new ConstraintViolation[] {})[0].getMessage());
	}
}
