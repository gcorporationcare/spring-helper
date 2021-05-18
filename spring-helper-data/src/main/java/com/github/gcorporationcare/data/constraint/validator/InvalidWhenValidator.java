package com.github.gcorporationcare.data.constraint.validator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.expression.spel.standard.SpelExpressionParser;

import com.github.gcorporationcare.data.constraint.InvalidExpression;
import com.github.gcorporationcare.data.constraint.InvalidWhen;

import lombok.extern.slf4j.Slf4j;

/**
 * Dealing with validation for
 * {@link com.github.gcorporationcare.data.constraint.InvalidWhen}
 */
@Slf4j
public class InvalidWhenValidator implements ConstraintValidator<InvalidWhen, Object> {

	private static final SpelExpressionParser PARSER = new SpelExpressionParser();
	private InvalidExpression[] expressions;

	@Override
	public void initialize(InvalidWhen constraintAnnotation) {
		expressions = constraintAnnotation.value();
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		List<InvalidExpression> notValid = Arrays.stream(expressions)
				.filter(e -> (Boolean) PARSER.parseExpression(e.value()).getValue(value)).collect(Collectors.toList());
		log.debug("Given expressions {} are not valid {}", expressions, notValid);
		boolean isValid = notValid.isEmpty();
		if (!isValid) {
			Arrays.stream(expressions).forEach(e -> context.buildConstraintViolationWithTemplate(e.message())
					.addPropertyNode(e.field()).addConstraintViolation());
		}
		return isValid;
	}
}