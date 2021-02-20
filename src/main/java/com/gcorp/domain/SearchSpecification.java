/**
 * 
 */
package com.gcorp.domain;

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

import com.gcorp.domain.SearchFilter.SearchFilterOperator;

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
		if (!(filter.getValue() instanceof LocalTime))
			return toPredicate(root, query, builder);

		Path<LocalTime> path = filter.getField().contains(PROPERTIES_DELIMITER)
				? this.<LocalTime>getParametizedPath(root, filter.getField()) : root.get(filter.getField());
		switch (filter.getOperator()) {
		case IS_GREATER_THAN:
			return builder.greaterThan(path, (LocalTime) filter.getValue());
		case IS_GREATER_THAN_OR_EQUAL:
			return builder.greaterThanOrEqualTo(path, (LocalTime) filter.getValue());
		case IS_LESS_THAN:
			return builder.lessThan(path, (LocalTime) filter.getValue());
		case IS_LESS_THAN_OR_EQUAL:
			return builder.lessThanOrEqualTo(path, (LocalTime) filter.getValue());
		default:
			return toPredicate(root, query, builder);
		}
	}

	private Predicate toLocalDatePredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
		if (!(filter.getValue() instanceof LocalDate))
			return toPredicate(root, query, builder);

		Path<LocalDate> path = filter.getField().contains(PROPERTIES_DELIMITER)
				? this.<LocalDate>getParametizedPath(root, filter.getField()) : root.get(filter.getField());
		switch (filter.getOperator()) {
		case IS_GREATER_THAN:
			return builder.greaterThan(path, (LocalDate) filter.getValue());
		case IS_GREATER_THAN_OR_EQUAL:
			return builder.greaterThanOrEqualTo(path, (LocalDate) filter.getValue());
		case IS_LESS_THAN:
			return builder.lessThan(path, (LocalDate) filter.getValue());
		case IS_LESS_THAN_OR_EQUAL:
			return builder.lessThanOrEqualTo(path, (LocalDate) filter.getValue());
		default:
			return toPredicate(root, query, builder);
		}
	}

	private Predicate toLocalDateTimePredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
		if (!(filter.getValue() instanceof LocalDateTime))
			return toPredicate(root, query, builder);

		Path<LocalDateTime> path = filter.getField().contains(PROPERTIES_DELIMITER)
				? this.<LocalDateTime>getParametizedPath(root, filter.getField()) : root.get(filter.getField());
		switch (filter.getOperator()) {
		case IS_GREATER_THAN:
			return builder.greaterThan(path, (LocalDateTime) filter.getValue());
		case IS_GREATER_THAN_OR_EQUAL:
			return builder.greaterThanOrEqualTo(path, (LocalDateTime) filter.getValue());
		case IS_LESS_THAN:
			return builder.lessThan(path, (LocalDateTime) filter.getValue());
		case IS_LESS_THAN_OR_EQUAL:
			return builder.lessThanOrEqualTo(path, (LocalDateTime) filter.getValue());
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
		String[] properties = field.split(PROPERTIES_SPLITTER);
		return getJoin(root, field).get(properties[properties.length - 1]);
	}

	public <U> Path<U> getParametizedPath(Root<T> root, String field) {
		String[] properties = field.split("\\.");
		return getJoin(root, field).<U>get(properties[properties.length - 1]);
	}

	@Override
	public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
		final List<SearchFilterOperator> notCommons = Arrays.asList(SearchFilterOperator.IS_GREATER_THAN,
				SearchFilterOperator.IS_GREATER_THAN_OR_EQUAL, SearchFilterOperator.IS_LESS_THAN,
				SearchFilterOperator.IS_LESS_THAN_OR_EQUAL);
		if (filter.getValue() instanceof LocalDateTime && notCommons.contains(filter.getOperator()))
			return toLocalDateTimePredicate(root, query, builder);
		else if (filter.getValue() instanceof LocalDate && notCommons.contains(filter.getOperator()))
			return toLocalDatePredicate(root, query, builder);
		else if (filter.getValue() instanceof LocalTime && notCommons.contains(filter.getOperator()))
			return toLocalTimePredicate(root, query, builder);
		if (filter.getField().contains(PROPERTIES_DELIMITER))
			return toPredicateForJoin(root, query, builder);
		String insensitiveValue = filter.getValue() != null ? filter.getValue().toString() : null;
		boolean insensitiveAllowed = String.class.isAssignableFrom(root.get(filter.getField()).getJavaType());
		switch (filter.getOperator()) {
		case IS_EQUAL:
			return builder.equal(root.get(filter.getField()), filter.getValue());
		case IS_EQUAL_CASE_INSENSITIVE:
			return insensitiveAllowed ? builder.like(root.<String>get(filter.getField()), insensitiveValue)
					: builder.equal(root.get(filter.getField()), filter.getValue());
		case IS_NOT_EQUAL:
			return builder.notEqual(root.get(filter.getField()), filter.getValue());
		case IS_NOT_EQUAL_CASE_INSENSITIVE:
			return insensitiveAllowed ? builder.notLike(root.<String>get(filter.getField()), insensitiveValue)
					: builder.notEqual(root.get(filter.getField()), filter.getValue());
		case IS_GREATER_THAN:
			return builder.greaterThan(root.<String>get(filter.getField()), filter.getValue().toString());
		case IS_GREATER_THAN_OR_EQUAL:
			return builder.greaterThanOrEqualTo(root.<String>get(filter.getField()), filter.getValue().toString());
		case IS_LESS_THAN:
			return builder.lessThan(root.<String>get(filter.getField()), filter.getValue().toString());
		case IS_LESS_THAN_OR_EQUAL:
			return builder.lessThanOrEqualTo(root.<String>get(filter.getField()), filter.getValue().toString());
		case IS_NULL:
			return builder.isNull(root.get(filter.getField()));
		case IS_NOT_NULL:
			return builder.isNotNull(root.get(filter.getField()));
		case IS_LIKE:
			return builder.like(root.<String>get(filter.getField()), "%" + filter.getValue().toString() + "%");
		case IS_NOT_LIKE:
			return builder.notLike(root.<String>get(filter.getField()), "%" + filter.getValue().toString() + "%");
		case IS_STARTING_WITH:
			return builder.like(root.<String>get(filter.getField()), filter.getValue().toString() + "%");
		case IS_ENDING_WITH:
			return builder.like(root.<String>get(filter.getField()), "%" + filter.getValue().toString());
		case IS_IN:
			Path<T> path = root.get(filter.getField());
			return path.in(getIsInValue(path, filter.getValue()));
		case IS_NOT_IN:
			Path<T> notPath = root.get(filter.getField());
			return builder.not(notPath.in(getIsInValue(notPath, filter.getValue())));
		}
		return null;

	}

	public Predicate toPredicateForJoin(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
		if (!filter.getField().contains(PROPERTIES_DELIMITER))
			return toPredicate(root, query, builder);
		String insensitiveValue = filter.getValue() != null ? filter.getValue().toString() : null;
		boolean insensitiveAllowed = String.class.isAssignableFrom(getPath(root, filter.getField()).getJavaType());
		switch (filter.getOperator()) {
		case IS_EQUAL:
			return builder.equal(getPath(root, filter.getField()), filter.getValue());
		case IS_EQUAL_CASE_INSENSITIVE:
			return insensitiveAllowed
					? builder.like(this.<String>getParametizedPath(root, filter.getField()), insensitiveValue)
					: builder.equal(getPath(root, filter.getField()), filter.getValue());
		case IS_NOT_EQUAL:
			return builder.notEqual(getPath(root, filter.getField()), filter.getValue());
		case IS_NOT_EQUAL_CASE_INSENSITIVE:
			return insensitiveAllowed
					? builder.notLike(this.<String>getParametizedPath(root, filter.getField()), insensitiveValue)
					: builder.notEqual(getPath(root, filter.getField()), filter.getValue());
		case IS_GREATER_THAN:
			return builder.greaterThan(this.<String>getParametizedPath(root, filter.getField()),
					filter.getValue().toString());
		case IS_GREATER_THAN_OR_EQUAL:
			return builder.greaterThanOrEqualTo(this.<String>getParametizedPath(root, filter.getField()),
					filter.getValue().toString());
		case IS_LESS_THAN:
			return builder.lessThan(this.<String>getParametizedPath(root, filter.getField()),
					filter.getValue().toString());
		case IS_LESS_THAN_OR_EQUAL:
			return builder.lessThanOrEqualTo(this.<String>getParametizedPath(root, filter.getField()),
					filter.getValue().toString());
		case IS_NULL:
			return builder.isNull(getPath(root, filter.getField()));
		case IS_NOT_NULL:
			return builder.isNotNull(getPath(root, filter.getField()));
		case IS_LIKE:
			return builder.like(this.<String>getParametizedPath(root, filter.getField()),
					"%" + filter.getValue().toString() + "%");
		case IS_NOT_LIKE:
			return builder.notLike(this.<String>getParametizedPath(root, filter.getField()),
					"%" + filter.getValue().toString() + "%");
		case IS_STARTING_WITH:
			return builder.like(this.<String>getParametizedPath(root, filter.getField()),
					filter.getValue().toString() + "%");
		case IS_ENDING_WITH:
			return builder.like(this.<String>getParametizedPath(root, filter.getField()),
					"%" + filter.getValue().toString());
		case IS_IN:
			Path<T> path = getPath(root, filter.getField());
			return path.in(getIsInValue(path, filter.getValue()));
		case IS_NOT_IN:
			Path<T> notPath = getPath(root, filter.getField());
			return builder.not(notPath.in(getIsInValue(notPath, filter.getValue())));
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
