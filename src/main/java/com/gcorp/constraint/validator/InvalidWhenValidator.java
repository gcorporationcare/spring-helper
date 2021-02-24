package com.gcorp.constraint.validator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.expression.spel.standard.SpelExpressionParser;

import com.gcorp.constraint.InvalidWhen;

import lombok.extern.slf4j.Slf4j;

/**
 * Dealing with validation for {@link com.gcorp.constraint.InvalidWhen}
 */
@Slf4j
public class InvalidWhenValidator implements ConstraintValidator<InvalidWhen, Object> {

	private static final SpelExpressionParser PARSER = new SpelExpressionParser();
	private String[] expressions;

	@Override
	public void initialize(InvalidWhen constraintAnnotation) {
		expressions = constraintAnnotation.value();
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		List<String> notValid = Arrays.stream(expressions)
				.filter(e -> (Boolean) PARSER.parseExpression(e).getValue(value)).collect(Collectors.toList());
		log.debug("Given expressions {} are not valid {}", expressions, notValid);
		boolean isValid = notValid.isEmpty();
		if (!isValid) {
			Arrays.stream(expressions)
					.forEach(e -> context
							.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
							.addPropertyNode(e).addConstraintViolation());
		}
		return isValid;
	}
}