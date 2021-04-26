package com.github.gcorporationcare.web.dto;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.github.gcorporationcare.web.domain.FieldFilter;
import com.github.gcorporationcare.web.domain.FieldFilterable;

import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor
@JsonFilter(FieldFilter.JSON_FILTER_NAME)
public abstract class BaseDto implements FieldFilterable {
	@Override
	public @NonNull Set<String> defaultFields() {
		return FieldFilterable.defaultFields(getClass(), BaseDto.class);
	}
}
