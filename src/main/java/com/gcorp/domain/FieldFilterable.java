package com.gcorp.domain;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.gcorp.annotation.DefaultField;
import com.gcorp.common.Utils;

import lombok.NonNull;

public interface FieldFilterable {

	/**
	 * List of fields that will be sent by default in API calls
	 * 
	 * @return the list of fields to return when user nothing is explicitly
	 *         requested
	 */
	@NonNull
	Set<String> defaultFields();

	static <S extends FieldFilterable, E extends FieldFilterable> Set<String> defaultFields(Class<S> startClazz,
			Class<E> endClazz) {
		String[] fields = Utils.getInheritedFields(startClazz, endClazz).stream()
				.filter(f -> f.isAnnotationPresent(DefaultField.class)).map(Field::getName).toArray(String[]::new);
		return new HashSet<>(Arrays.asList(fields));
	}
}
