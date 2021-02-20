package com.gcorp.common;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * API field error referrer Used for representing errors occurring when
 * validating an entity
 */
@Getter
@AllArgsConstructor
public class ApiFieldError implements Serializable {
	private static final long serialVersionUID = 1L;
	private String field;
	private String code;
	private Serializable rejectedValue;
}
