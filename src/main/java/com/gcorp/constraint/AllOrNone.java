package com.gcorp.constraint;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.gcorp.constraint.validator.AllOrNoneValidator;
import com.gcorp.i18n.I18nMessage;

/**
 * Constraints for dependent fields<br>
 * Will force all listed fields to have a non-null value when at least one has,
 * Otherwise will force the whole group to have null values
 */
@Documented
@Constraint(validatedBy = AllOrNoneValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AllOrNone {

	/**
	 * Array of fields that are linked together If any of these fields is non-null,
	 * then all others must be non-null. In case any of these fields is null, all
	 * the others must be as well.
	 */
	String[] value();

	/**
	 * The error message that will be displayed
	 */
	String message() default I18nMessage.DataError.MULTIPLE_OR_NONE_EXPECTED;

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
