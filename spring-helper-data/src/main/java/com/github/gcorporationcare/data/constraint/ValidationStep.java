package com.github.gcorporationcare.data.constraint;

/**
 * Collection of interfaces for different validation steps From Simple to
 * Complex<br>
 */
public interface ValidationStep {

	/**
	 * For validating mandatory fields when patching.
	 */
	public interface OnPatch {
	}

	/**
	 * For validating mandatory fields when creating (fields like Id does not need
	 * to be present when creating).
	 */
	public interface OnCreate {
	}

	/**
	 * For validating mandatory fields when updating (fields like Id are mandatory
	 * while audit fields like createdBy are not).
	 */
	public interface OnUpdate extends OnPatch {
	}

	public interface OnCreateAndUpdate extends OnCreate, OnUpdate {
	}
}
