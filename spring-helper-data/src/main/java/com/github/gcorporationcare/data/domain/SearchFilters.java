package com.github.gcorporationcare.data.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.github.gcorporationcare.data.domain.SearchFilter.SearchFilterOperator;

/**
 * Group of filters
 */
public final class SearchFilters<T> {

	private StringBuilder sb;
	private List<SearchFilter> internalFilters;

	public SearchFilters() {
		internalFilters = new ArrayList<>();
		sb = new StringBuilder();
	}

	private void updateFilters(SearchFilter filter) {
		if (sb.length() != 0) {
			sb.append(SearchFilterOperator.FILTERS_DELIMITER);
		}
		sb.append(filter);
	}

	public static <T> SearchFilters<T> of(String field, SearchFilterOperator operator, Serializable value) {
		return new SearchFilters<T>().and(field, operator, value);
	}

	public static <T> SearchFilters<T> of(String field, String sign, Serializable value) {
		return new SearchFilters<T>().and(field, sign, value);
	}

	public static <T> SearchFilters<T> of(SearchFilter filter) {
		return new SearchFilters<T>().and(filter);
	}

	public SearchFilters<T> and(String field, SearchFilterOperator operator, Serializable value) {
		SearchFilter filter = new SearchFilter(true, field, operator, value);
		internalFilters.add(filter);
		updateFilters(filter);
		return this;
	}

	public SearchFilters<T> and(String field, String sign, Serializable value) {
		return and(field, SearchFilterOperator.valueOf(sign), value);
	}

	public SearchFilters<T> and(SearchFilter filter) {
		return and(filter.getField(), filter.getOperator(), filter.getValue());
	}

	public SearchFilters<T> or(String field, SearchFilterOperator operator, Serializable value) {
		SearchFilter filter = new SearchFilter(false, field, operator, value);
		internalFilters.add(filter);
		updateFilters(filter);
		return this;
	}

	public SearchFilters<T> or(String field, String sign, Serializable value) {
		return or(field, SearchFilterOperator.valueOf(sign), value);
	}

	public SearchFilters<T> or(SearchFilter filter) {
		return or(filter.getField(), filter.getOperator(), filter.getValue());
	}

	public Specification<T> toSpecifications() {
		List<Specification<T>> andSpecifications = new ArrayList<>();
		// We start by grouping all the and together<br/>
		// A x B + C x E = (AxB) + (CxE)
		for (SearchFilter f : internalFilters) {
			if (!f.isAnd() || andSpecifications.isEmpty()) {
				Specification<T> specification = Specification.where(new SearchSpecification<>(f));
				andSpecifications.add(specification);
				continue;
			}
			int lastIndex = andSpecifications.size() - 1;
			Specification<T> lastSpecification = andSpecifications.get(lastIndex);
			andSpecifications.set(lastIndex, lastSpecification.and(new SearchSpecification<>(f)));
		}
		Specification<T> orSpecifications = Specification.where(null);
		for (Specification<T> specification : andSpecifications) {
			orSpecifications = orSpecifications.or(specification);
		}
		return orSpecifications;
	}

	/**
	 * Generate filter from a given String
	 * 
	 * @param <T>     the entity class we are searching on
	 * @param filters the string version of the filters to apply
	 * @return an instance containing the list of specifications on the lookup
	 */
	public static <T> SearchFilters<T> fromString(String filters) {
		SearchFilters<T> result = new SearchFilters<>();
		result.internalFilters = new ArrayList<>(Arrays.asList(SearchFilter.filtersFromString(filters)));
		return result;
	}

	private SearchFilters<T> concat(boolean and, SearchFilters<T> filters) {
		SearchFilters<T> concatedFilters = new SearchFilters<>();
		concatedFilters.internalFilters = new ArrayList<>(
				Arrays.asList(SearchFilter.concat(and, internalFilters.toArray(new SearchFilter[] {}),
						filters.internalFilters.toArray(new SearchFilter[] {}))));
		return concatedFilters;
	}

	public SearchFilters<T> and(SearchFilters<T> filters) {
		return concat(true, filters);
	}

	public SearchFilters<T> or(SearchFilters<T> filters) {
		return concat(false, filters);
	}

	@Override
	public String toString() {
		return sb.toString();
	}
}
