package com.github.gcorporationcare.data.domain;

@FunctionalInterface
public interface Formattable {
	/**
	 * Formatting fields i.e. Transforming some fields in lower case, etc...
	 */
	void format();
}
