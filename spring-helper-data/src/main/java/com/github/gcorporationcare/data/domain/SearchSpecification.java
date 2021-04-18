/**
 * 
 */
package com.github.gcorporationcare.data.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.github.gcorporationcare.data.domain.SearchFilter.SearchFilterOperator;

/**
 * Custom specification object implementing Spring one
 */
public class SearchSpecification<T> implements Specification<T> {

	private static final long serialVersionUID = 1L;

	public static final String IS_IN_DELIMITER = "&";
	public static final String PROPERTIES_DELIMITER = ".";
	public static final String PROPERTIES_SPLITTER = "\\.";

	private SearchFilter filter;

	public SearchSpecification(SearchFilter filter) {
		super();
		this.filter = filter;
	}

	private Predicate toLocalTimePredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
		Path<LocalTime> path = filter.getField().contains(PROPERTIES_DELIMITER)
				? this.<LocalTime>getParametizedPath(root, filter.getField())
				: root.get(filter.getField());
		Serializable safeValue = filter.getSafeValue(path, filter.getValue());
		if (!(filter.getValue() instanceof LocalTime))
			return toPredicate(root, query, builder);

		switch (filter.getOperator()) {
		case IS_GREATER_THAN:
			return builder.greaterThan(path, (LocalTime) safeValue);
		case IS_GREATER_THAN_OR_EQUAL:
			return builder.greaterThanOrEqualTo(path, (LocalTime) safeValue);
		case IS_LESS_THAN:
			return builder.lessThan(path, (LocalTime) safeValue);
		case IS_LESS_THAN_OR_EQUAL:
			return builder.lessThanOrEqualTo(path, (LocalTime) safeValue);
		default:
			return toPredicate(root, query, builder);
		}
	}

	private Predicate toLocalDatePredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
		Path<LocalDate> path = filter.getField().contains(PROPERTIES_DELIMITER)
				? this.<LocalDate>getParametizedPath(root, filter.getField())
				: root.get(filter.getField());
		Serializable safeValue = filter.getSafeValue(path, filter.getValue());
		if (!(filter.getValue() instanceof LocalDate))
			return toPredicate(root, query, builder);

		switch (filter.getOperator()) {
		case IS_GREATER_THAN:
			return builder.greaterThan(path, (LocalDate) safeValue);
		case IS_GREATER_THAN_OR_EQUAL:
			return builder.greaterThanOrEqualTo(path, (LocalDate) safeValue);
		case IS_LESS_THAN:
			return builder.lessThan(path, (LocalDate) safeValue);
		case IS_LESS_THAN_OR_EQUAL:
			return builder.lessThanOrEqualTo(path, (LocalDate) safeValue);
		default:
			return toPredicate(root, query, builder);
		}
	}

	private Predicate toLocalDateTimePredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
		Path<LocalDateTime> path = filter.getField().contains(PROPERTIES_DELIMITER)
				? this.<LocalDateTime>getParametizedPath(root, filter.getField())
				: root.get(filter.getField());
		Serializable safeValue = filter.getSafeValue(path, filter.getValue());
		if (!(safeValue instanceof LocalDateTime))
			return toPredicate(root, query, builder);

		switch (filter.getOperator()) {
		case IS_GREATER_THAN:
			return builder.greaterThan(path, (LocalDateTime) safeValue);
		case IS_GREATER_THAN_OR_EQUAL:
			return builder.greaterThanOrEqualTo(path, (LocalDateTime) safeValue);
		case IS_LESS_THAN:
			return builder.lessThan(path, (LocalDateTime) safeValue);
		case IS_LESS_THAN_OR_EQUAL:
			return builder.lessThanOrEqualTo(path, (LocalDateTime) safeValue);
		default:
			return toPredicate(root, query, builder);
		}
	}

	private Join<T, T> getJoin(Root<T> root, String field) {
		String[] properties = field.split(PROPERTIES_SPLITTER);
		Join<T, T> joined = root.join(properties[0], JoinType.INNER);
		if (properties.length > 2) {
			for (int index = 1; index < properties.length - 1; index++) {
				joined = joined.join(properties[index], JoinType.INNER);
			}
		}
		return joined;
	}

	public Path<T> getPath(Root<T> root, String field) {
		if (!field.contains(PROPERTIES_DELIMITER))
			return root.get(field);
		String[] properties = field.split(PROPERTIES_SPLITTER);
		return getJoin(root, field).get(properties[properties.length - 1]);
	}

	public <U> Path<U> getParametizedPath(Root<T> root, String field) {
		if (!field.contains(PROPERTIES_DELIMITER))
			return root.<U>get(field);
		String[] properties = field.split("\\.");
		return getJoin(root, field).<U>get(properties[properties.length - 1]);
	}

	@Override
	public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
		final List<SearchFilterOperator> notCommons = Arrays.asList(SearchFilterOperator.IS_GREATER_THAN,
				SearchFilterOperator.IS_GREATER_THAN_OR_EQUAL, SearchFilterOperator.IS_LESS_THAN,
				SearchFilterOperator.IS_LESS_THAN_OR_EQUAL);
		Path<T> path = getPath(root, filter.getField());
		Path<String> pathString = this.<String>getParametizedPath(root, filter.getField());
		Serializable safeValue = filter.getSafeValue(path, filter.getValue());
		String safeStringValue = filter.getSafeStringValue(path, safeValue);
		if (safeValue instanceof LocalDateTime && notCommons.contains(filter.getOperator()))
			return toLocalDateTimePredicate(root, query, builder);
		else if (safeValue instanceof LocalDate && notCommons.contains(filter.getOperator()))
			return toLocalDatePredicate(root, query, builder);
		else if (safeValue instanceof LocalTime && notCommons.contains(filter.getOperator()))
			return toLocalTimePredicate(root, query, builder);
		boolean insensitiveAllowed = String.class.isAssignableFrom(path.getJavaType());
		switch (filter.getOperator()) {
		case IS_EQUAL:
			return builder.equal(path, safeValue);
		case IS_EQUAL_CASE_INSENSITIVE:
			return insensitiveAllowed ? builder.like(pathString, safeStringValue) : builder.equal(path, safeValue);
		case IS_NOT_EQUAL:
			return builder.notEqual(path, safeValue);
		case IS_NOT_EQUAL_CASE_INSENSITIVE:
			return insensitiveAllowed ? builder.notLike(pathString, safeStringValue)
					: builder.notEqual(path, safeValue);
		case IS_GREATER_THAN:
			return builder.greaterThan(pathString, safeStringValue);
		case IS_GREATER_THAN_OR_EQUAL:
			return builder.greaterThanOrEqualTo(pathString, safeStringValue);
		case IS_LESS_THAN:
			return builder.lessThan(pathString, safeStringValue);
		case IS_LESS_THAN_OR_EQUAL:
			return builder.lessThanOrEqualTo(pathString, safeStringValue);
		case IS_NULL:
			return builder.isNull(path);
		case IS_NOT_NULL:
			return builder.isNotNull(path);
		case IS_LIKE:
			return builder.like(pathString, "%" + safeStringValue + "%");
		case IS_NOT_LIKE:
			return builder.notLike(pathString, "%" + safeStringValue + "%");
		case IS_STARTING_WITH:
			return builder.like(pathString, safeStringValue + "%");
		case IS_ENDING_WITH:
			return builder.like(pathString, "%" + safeStringValue);
		case IS_IN:
			return path.in(getIsInValue(path, safeValue));
		case IS_NOT_IN:
			return builder.not(path.in(getIsInValue(path, safeValue)));
		}
		return null;
	}

	private Object[] getIsInValue(Path<T> path, Object value) {
		if (value == null)
			return new Object[] {};
		String[] values = value.toString().split(IS_IN_DELIMITER);
		if (Long.class.isAssignableFrom(path.getJavaType())) {
			return Arrays.stream(values).map(Long::valueOf).collect(Collectors.toList()).toArray();
		}
		return values;
	}
}
