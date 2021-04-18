package com.github.gcorporationcare.data.constraint;

import org.junit.jupiter.api.Test;

import com.github.gcorporationcare.notest.common.DataProviderTestHelper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

class InvalidWhenTest extends DataProviderTestHelper {

	private static final String EXPECTED_MESSAGE = "We are all expecting to see this message";

	public enum SimpleNumber {
		ONE(1), TWO(2), THREE(3);

		public final int value;

		SimpleNumber(int value) {
			this.value = value;
		}
	}

	@AllArgsConstructor
	@InvalidWhen(value = { "numberName == null || numberName.value != numberValue" }, message = EXPECTED_MESSAGE)
	public class SimpleTestClass {
		@Getter
		@Setter
		Integer numberValue;
		@Getter
		@Setter
		SimpleNumber numberName;
	}

	@Test
	void testValidation() {
		final int expectedViolations = 2;
		validateConstraint(new SimpleTestClass(SimpleNumber.TWO.value, SimpleNumber.THREE), expectedViolations,
				EXPECTED_MESSAGE);
		validateConstraint(new SimpleTestClass(SimpleNumber.THREE.value, SimpleNumber.TWO), expectedViolations,
				EXPECTED_MESSAGE);
		validateConstraint(new SimpleTestClass(SimpleNumber.ONE.value, SimpleNumber.THREE), expectedViolations,
				EXPECTED_MESSAGE);
		validateConstraint(new SimpleTestClass(SimpleNumber.ONE.value, SimpleNumber.ONE), 0, EXPECTED_MESSAGE);
	}
}
