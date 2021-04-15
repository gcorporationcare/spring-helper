package com.gcorp.domain;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.Serializable;

import org.junit.jupiter.api.Test;

import com.gcorp.domain.SearchFilter.SearchFilterOperator;

class SearchFilterTest {

	static final String FIELD = "field";
	static final String SIGN = "=";
	static final Serializable VALUE = 5.6;

	enum Bytes {
		ZERO, ONE
	}

	@Test
	void testConstructor_WithInvalidFields() {
		// Null field
		try {
			new SearchFilter(false, null, SearchFilterOperator.IS_EQUAL, VALUE);
		} catch (IllegalArgumentException e) {
			assertNotNull(e);
		}

		// Don't allow null values field
		try {
			new SearchFilter(false, FIELD, SearchFilterOperator.IS_EQUAL, null);
		} catch (IllegalArgumentException e) {
			assertNotNull(e);
		}

		// Invalid operator sign
		try {
			new SearchFilter(false, FIELD, "==", VALUE);
		} catch (IllegalArgumentException e) {
			assertNotNull(e);
		}

		// Operator is not force
		try {
			new SearchFilter(false, FIELD, "=");
		} catch (IllegalArgumentException e) {
			assertNotNull(e);
		}
	}

	@Test
	void testConstructor_WithValidFields() {
		// Null field
		assertNotNull(new SearchFilter(false, FIELD, SearchFilterOperator.IS_EQUAL, VALUE));
		assertNotNull(new SearchFilter(false, FIELD, "=", VALUE));
		assertNotNull(new SearchFilter(false, FIELD, "0"));
	}

	@Test
	void testFromString() {
		final String d = SearchFilterOperator.FILTER_OPERATOR_DELIMITER;
		SearchFilter filter1 = new SearchFilter(true, FIELD, SearchFilterOperator.IS_EQUAL, VALUE);
		String string1 = filter1.toString();
		String expected1 = String.format("%s%s%s%s%s%s", SearchFilterOperator.FILTER_AND, FIELD, d,
				SearchFilterOperator.IS_EQUAL.getSymbol(), d, VALUE);
		assertEquals(expected1, string1);
		assertEquals(filter1, SearchFilter.fromString(string1));
		SearchFilter filter2 = new SearchFilter(true, FIELD, SearchFilterOperator.IS_NOT_NULL);
		String string2 = filter2.toString();
		String expected2 = String.format("%s%s%s%s", SearchFilterOperator.FILTER_AND, FIELD, d,
				SearchFilterOperator.IS_NOT_NULL.getSymbol());
		assertEquals(expected2, string2);
		assertEquals(filter2, SearchFilter.fromString(string2));
	}

	@Test
	void testSearchFilters() {
		// Bad order field
		try {
			SearchFilters.fromString(String.format("%s%s%s",
					new SearchFilter(true, FIELD, SearchFilterOperator.IS_EQUAL, VALUE),
					new SearchFilter(true, FIELD, SearchFilterOperator.IS_NOT_NULL), SearchFilterOperator.FILTER_OR));
		} catch (IllegalArgumentException e) {
			assertNotNull(e);
		}
		SearchFilters<Object> filters = SearchFilters.fromString(String.format("%s%s%s",
				new SearchFilter(true, FIELD, SearchFilterOperator.IS_EQUAL, VALUE), SearchFilterOperator.FILTER_OR,
				new SearchFilter(true, FIELD, SearchFilterOperator.IS_NOT_NULL)));
		assertNotNull(filters);
	}

	@Test
	void testFromString_WithNull() {
		assertThrows(IllegalArgumentException.class, () -> SearchFilter.fromString(null));
	}

	@Test
	void testFromString_WithInvalidField() {
		assertThrows(IllegalArgumentException.class, () -> SearchFilter.fromString("1"));
	}

	@Test
	void testSearchFilter_WithSingleOperator() {
		assertThrows(IllegalArgumentException.class,
				() -> new SearchFilter(true, "alpha", SearchFilterOperator.IS_EQUAL));
	}

	@Test
	void testSearchFilter_WithoutOperator() {
		SearchFilterOperator no = null;
		assertThrows(IllegalArgumentException.class, () -> new SearchFilter(true, "alpha", no, null));
	}

	@Test
	void testAndOr() {
		final SearchFilterOperator operator = SearchFilterOperator.IS_EQUAL;
		final String field = "field";
		final long value = 2;
		final SearchFilter andItemFilter = new SearchFilter(true, field, operator, value);
		final SearchFilter orItemFilter = new SearchFilter(false, field, operator, value);
		final SearchFilter[] filters1 = new SearchFilter[] { andItemFilter, orItemFilter, orItemFilter };
		final SearchFilter[] filters2 = new SearchFilter[] { andItemFilter, orItemFilter, andItemFilter };

		assertArrayEquals(new SearchFilter[] { andItemFilter, orItemFilter, orItemFilter, andItemFilter },
				SearchFilter.compute(andItemFilter, andItemFilter, orItemFilter, orItemFilter));

		// (A + B + C) * (D + E * F) = ((A & D) | (B & D) | (C & D) | (A & E &
		// F) | (B & E & F) | (C & E & F))
		assertNotNull(SearchFilter.concat(true, filters1, filters2));
		// (A + B + C) + (D + E * F) = A + B + C + D + E * F
		assertNotNull(SearchFilter.concat(false, filters2, filters1));

		// (A + B + C) * (D + E * F) = ((A & D) | (B & D) | (C & D) | (A & E &
		// F) | (B & E & F) | (C & E & F))
		assertArrayEquals(SearchFilter.filtersFromString(
				"-a,=,a;-d,=,d;|b,=,b;-d,=,d;|c,=,c;-d,=,d;|a,=,a;-e,=,e;-f,=,f;|b,=,b;-e,=,e;-f,=,f;|c,=,c;-e,=,e;-f,=,f"),
				SearchFilter.concat(true, SearchFilter.filtersFromString("-a,=,a;|b,=,b;|c,=,c"),
						SearchFilter.filtersFromString("-d,=,d;|e,=,e;-f,=,f")));
	}
}
