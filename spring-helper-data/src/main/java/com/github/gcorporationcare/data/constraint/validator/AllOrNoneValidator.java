package com.github.gcorporationcare.data.constraint.validator;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.expression.spel.standard.SpelExpressionParser;

import com.github.gcorporationcare.data.constraint.AllOrNone;

/**
 * Dealing with validation for {@link com.gcorp.constraint.AllOrNone}
 */
public class AllOrNoneValidator implements ConstraintValidator<AllOrNone, Object> {
	private static final SpelExpressionParser PARSER = new SpelExpressionParser();
	private String[] fields;

	@Override
	public void initialize(AllOrNone constraintAnnotation) {
		fields = constraintAnnotation.value();
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		long notNull = Stream.of(fields).map(field -> PARSER.parseExpression(field).getValue(value))
				.filter(Objects::nonNull).count();
		boolean isValid = notNull == 0 || notNull == fields.length;
		if (!isValid) {
			context.disableDefaultConstraintViolation();
			Arrays.stream(fields)
					.forEach(f -> context
							.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
							.addPropertyNode(f).addConstraintViolation());
		}
		return isValid;
	}
}