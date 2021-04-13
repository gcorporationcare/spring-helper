package com.gcorp.constraint.validator;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.gcorp.constraint.LanguageCode;

/**
 * Dealing with validation for {@link com.gcorp.constraint.LanguageCode}
 */
public class LanguageCodeValidator implements ConstraintValidator<LanguageCode, Object> {

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		if (value == null) {
			return false;
		}
		List<String> languages = Arrays.asList(Locale.getISOLanguages());
		return languages.contains(value.toString());
	}

}