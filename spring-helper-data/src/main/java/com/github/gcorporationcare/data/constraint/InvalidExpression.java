package com.github.gcorporationcare.data.constraint;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.github.gcorporationcare.data.i18n.I18nMessage;

/**
 * Sometimes, some field's value are inconsistent one to another<br>
 * This will force the fields not to be in such a situation
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface InvalidExpression {
	/**
	 * A logical expression using the properties of the class annotated by
	 * com.github.gcorporationcare.data.constraint.InvalidWhen. For expression
	 * syntax, see Spring
	 * {@link org.springframework.expression.spel.standard.SpelExpressionParser}l
	 * 
	 * @return the expression to parse
	 */
	String value();

	/**
	 * Any field of the class annotated by
	 * com.github.gcorporationcare.data.constraint.InvalidWhen that will be used in
	 * the validation error context.
	 * 
	 * @return the field of the name to link the validation error to
	 */
	String field();

	/**
	 * The error message that will be displayed
	 * 
	 * @return the message to use in exception when is not valid
	 */
	String message() default I18nMessage.DataError.INCONSISTENT_VALUE_GIVEN;
}
