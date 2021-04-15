package com.gcorp.domain;

@FunctionalInterface
public interface Constrainable {
	/**
	 * Trigger manual validation of constraints
	 * 
	 * @throws com.gcorp.exception.ValidationException when some violations exist
	 */
	void validate();
}