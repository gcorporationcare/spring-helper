package com.github.gcorporationcare.web.constraint;

import javax.validation.groups.Default;

/**
 * Collection of interfaces for different validation steps From Simple to
 * Complex<br>
 * . Do not use it in entities since BaseEntity.validateObject will only
 * validate default annotations
 */
public interface ValidationStep {

	/**
	 * For validating constrained fields when patching. If used on Validated
	 * annotation, OnPatch and Default constraints will be applied.
	 */
	public interface OnPatch extends Default {
	}

	/**
	 * For validating constrained fields when creating (fields like Id does not need
	 * to be present when creating). If used on Validated annotation, OnCreate and
	 * Default constraints will be applied.
	 */
	public interface OnCreate extends Default {
	}

	/**
	 * For validating constrained fields when updating (fields like Id are mandatory
	 * while audit fields like createdBy are not). If used on Validated annotation,
	 * OnUpdate, OnPatch and Default constraints will be applied.
	 */
	public interface OnUpdate extends OnPatch {
	}

	/**
	 * For validating all constrained fields. If used on Validated annotation,
	 * OnCreate, OnUpdate, OnPatch and Default constraints will be applied.
	 */
	public interface OnCreateAndUpdate extends OnCreate, OnUpdate {
	}
}
