package com.gcorp.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Indicates the default sorting when browsing database
 * 
 * @see com.gcorp.annotation.Configure
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldSort {
	/**
	 * The field's name
	 * 
	 * @return the name of the field to sort's on
	 */
	String value();

	/**
	 * If results must be sorted by ascending or descending order
	 * 
	 * @return true by default
	 */
	boolean ascending() default true;
}
