package com.github.gcorporationcare.data.constraint;

import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.validation.ValidationException;

import org.junit.jupiter.api.Test;

import com.github.gcorporationcare.data.i18n.I18nMessage;
import com.github.gcorporationcare.notest.common.DataProviderTestHelper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

class AllOrNoneTest extends DataProviderTestHelper {

	static final String FIELD_1 = "field1";
	static final String FIELD_2 = "field2";

	@AllArgsConstructor
	@AllOrNone({ FIELD_1, FIELD_2 })
	class SimpleClass1 {
		@Getter
		@Setter
		String field1;
		@Getter
		@Setter
		Boolean field2;
	}

	@AllArgsConstructor
	@AllOrNone({ FIELD_1, FIELD_2 })
	class SimpleClass2 {
		@Getter
		@Setter
		String field1;
		@Getter
		@Setter
		int field2;
		@Getter
		@Setter
		Double field3;
	}

	@AllArgsConstructor
	@AllOrNone({ "field1" })
	class SimpleClass3 {
		@Getter
		@Setter
		String field1;
	}

	@AllArgsConstructor
	@AllOrNone({ "field1" })
	class SimpleClass4 {
	}

	@Test
	void testValidation_WithBadClass() {
		assertThrows(ValidationException.class, () -> validateConstraint(new SimpleClass4(), 1, null));
	}

	@Test
	void testValidation() {
		final int zeroViolation = 0;
		final int twoViolations = 2;
		String[] expectedMessages = new String[] { I18nMessage.DataError.MULTIPLE_OR_NONE_EXPECTED,
				I18nMessage.DataError.MULTIPLE_OR_NONE_EXPECTED };
		validateConstraint(new SimpleClass1(FIELD_1, null), twoViolations, expectedMessages);
		validateConstraint(new SimpleClass1(FIELD_1, true), zeroViolation, null);

		validateConstraint(new SimpleClass2(null, zeroViolation, 2.2), twoViolations, expectedMessages);
		validateConstraint(new SimpleClass2(FIELD_1, zeroViolation, null), zeroViolation, null);

		validateConstraint(new SimpleClass3(null), zeroViolation, null);
		validateConstraint(new SimpleClass3(FIELD_1), zeroViolation, null);
	}
}
