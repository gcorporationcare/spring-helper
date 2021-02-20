package com.gcorp.constraint;

import javax.validation.groups.Default;

/**
 * Collection of interfaces for different validation steps From Simple to
 * Complex<br>
 * Simple being for validating recurrent fields used for search (user's name for
 * example) Medium for intermediary state (an order without its code for
 * example) Complex for most of the fields, without the one that are for
 * technical purposes (createdBy for example)<br>
 * Validation on a complex type will trigger validation on simpler field
 */
public interface ValidationStep {

	/**
	 * Group for validating recurrent fields used for search (user's name for
	 * example)
	 */
	public interface Simple extends Default {
	}

	/**
	 * Group for validating intermediary state fields (an order without its code for
	 * example)
	 */
	public interface Medium extends Simple {
	}

	/**
	 * Group for validating most of the fields, without the one that are for
	 * technical purposes (createdBy for example)
	 */
	public interface Complex extends Medium {
	}

	/**
	 * Group for validating system fields (createdBy for example)
	 */
	public interface System extends Complex {
	}
}
