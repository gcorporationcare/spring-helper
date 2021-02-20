package com.gcorp.domain;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

public interface PropertyPath {

	public static final String PROPERTY_SPLITTER_FOR_SPLIT = "\\.";
	public static final String PROPERTY_SPLITTER_FOR_CONTAINS = ".";

	public static <T> Join<T, T> getJoin(Root<T> root, String field) {
		String[] properties = field.split(PROPERTY_SPLITTER_FOR_SPLIT);
		Join<T, T> joined = root.join(properties[0], JoinType.INNER);
		if (properties.length > 2) {
			for (int index = 1; index < properties.length - 1; index++) {
				joined = joined.join(properties[index], JoinType.INNER);
			}
		}
		return joined;
	}

	public static <T> Path<T> getPath(Root<T> root, String field) {
		String[] properties = field.split(PROPERTY_SPLITTER_FOR_SPLIT);
		return getJoin(root, field).get(properties[properties.length - 1]);
	}

	public static <T, U> Path<U> getParametizedPath(Root<T> root, String field) {
		if (!field.contains(PROPERTY_SPLITTER_FOR_CONTAINS))
			return root.get(field);
		String[] properties = field.split(PROPERTY_SPLITTER_FOR_SPLIT);
		return getJoin(root, field).<U>get(properties[properties.length - 1]);
	}
}
