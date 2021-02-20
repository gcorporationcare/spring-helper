package com.gcorp.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this annotation for defining default sorting attribute on a given entity
 * class
 *
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Configure {
	/**
	 * Configuration of the default sorting attribute when browsing database
	 */
	FieldSort[] defaultSort() default {};
}
