package com.github.gcorporationcare.data.constraint;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.github.gcorporationcare.data.constraint.validator.LanguageCodeValidator;
import com.github.gcorporationcare.data.i18n.I18nMessage;

/**
 * Constraints for language field<br>
 * Will force value to be a valid ISO language code
 */
@Documented
@Constraint(validatedBy = LanguageCodeValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LanguageCode {

	/**
	 * The error message that will be displayed
	 * 
	 * @return the message to use in exception when is not valid
	 */
	String message() default I18nMessage.DataError.LANGUAGE_CODE_EXPECTED;

	/**
	 * The groups where the constraint will be applied
	 * 
	 * @return the list of groups to apply the constraint on
	 */
	Class<?>[] groups() default {};

	/**
	 * Payload type that can be attached to a given constraint declaration.
	 * 
	 * Payloads are typically used to carry on metadata information consumed by a
	 * validation client.
	 * 
	 * @return the list of payload classes
	 */
	Class<? extends Payload>[] payload() default {};
}
