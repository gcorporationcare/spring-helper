package com.gcorp.domain;

import java.util.Set;

import lombok.NonNull;

@FunctionalInterface
public interface FieldFilterable {

	/**
	 * List the fields that will be rendered when non selected
	 * 
	 * @return
	 */
	@NonNull
	Set<String> defaultFields();
}
