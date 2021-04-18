package com.github.gcorporationcare.data.domain;

@FunctionalInterface
public interface Constrainable {
	/**
	 * Trigger manual validation of constraints
	 * 
	 * @throws com.github.gcorporationcare.data.exception.ValidationException when some violations exist
	 */
	void validate();
}
