package com.gcorp.constraint;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import org.springframework.expression.spel.standard.SpelExpressionParser;

import com.gcorp.constraint.validator.InvalidWhenValidator;
import com.gcorp.i18n.I18nMessage;

/**
 * Sometimes, some field's value are inconsistent one to another<br>
 * This will force the fields not to be in such a situation
 */
@Documented
@Constraint(validatedBy = InvalidWhenValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface InvalidWhen {
	/**
	 * Enter here an array of expressions which will be parsed by
	 * {@link SpelExpressionParser}<br>
	 * Every single expression should determine when <strong>an instance of the
	 * annotated object is not valid</strong>
	 */
	String[] value();

	String message() default I18nMessage.DataError.INCONSISTENT_VALUE_GIVEN;

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
