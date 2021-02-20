package com.gcorp.constraint;

import javax.validation.ValidationException;

import org.junit.Test;

import com.gcorp.i18n.I18nMessage;
import com.gcorp.notest.DataProviderTestHelper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class AllOrNoneTest extends DataProviderTestHelper {

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

	@Test(expected = ValidationException.class)
	public void testValidation_WithBadClass() {
		validateConstraint(new SimpleClass4(), 1, null);
	}

	@Test
	public void testValidation() {
		final int zeroViolation = 0;
		final int twoViolations = 2;
		validateConstraint(new SimpleClass1(FIELD_1, null), twoViolations, I18nMessage.DataError.MULTIPLE_OR_NONE_EXPECTED);
		validateConstraint(new SimpleClass1(FIELD_1, true), zeroViolation, I18nMessage.DataError.MULTIPLE_OR_NONE_EXPECTED);

		validateConstraint(new SimpleClass2(null, zeroViolation, 2.2), twoViolations,
				I18nMessage.DataError.MULTIPLE_OR_NONE_EXPECTED);
		validateConstraint(new SimpleClass2(FIELD_1, zeroViolation, null), zeroViolation,
				I18nMessage.DataError.MULTIPLE_OR_NONE_EXPECTED);

		validateConstraint(new SimpleClass3(null), zeroViolation, I18nMessage.DataError.MULTIPLE_OR_NONE_EXPECTED);
		validateConstraint(new SimpleClass3(FIELD_1), zeroViolation, I18nMessage.DataError.MULTIPLE_OR_NONE_EXPECTED);
	}
}
