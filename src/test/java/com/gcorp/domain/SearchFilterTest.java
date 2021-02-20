package com.gcorp.domain;

import java.io.Serializable;

import org.junit.Assert;
import org.junit.Test;

import com.gcorp.domain.SearchFilter.SearchFilterOperator;

public class SearchFilterTest {

	static final String FIELD = "field";
	static final String SIGN = "=";
	static final Serializable VALUE = 5.6;

	@Test
	public void testConstructor_WithInvalidFields() {
		// Null field
		try {
			new SearchFilter(false, null, SearchFilterOperator.IS_EQUAL, VALUE);
		} catch (IllegalArgumentException e) {
			Assert.assertNotNull(e);
		}

		// Don't allow null values field
		try {
			new SearchFilter(false, FIELD, SearchFilterOperator.IS_EQUAL, null);
		} catch (IllegalArgumentException e) {
			Assert.assertNotNull(e);
		}

		// Invalid operator sign
		try {
			new SearchFilter(false, FIELD, "==", VALUE);
		} catch (IllegalArgumentException e) {
			Assert.assertNotNull(e);
		}

		// Operator is not force
		try {
			new SearchFilter(false, FIELD, "=");
		} catch (IllegalArgumentException e) {
			Assert.assertNotNull(e);
		}
	}

	@Test
	public void testConstructor_WithValidFields() {
		// Null field
		Assert.assertNotNull(new SearchFilter(false, FIELD, SearchFilterOperator.IS_EQUAL, VALUE));
		Assert.assertNotNull(new SearchFilter(false, FIELD, "=", VALUE));
		Assert.assertNotNull(new SearchFilter(false, FIELD, "0"));
	}

	@Test
	public void testFromString() {
		final String d = SearchFilterOperator.FILTER_OPERATOR_DELIMITER;
		SearchFilter filter1 = new SearchFilter(true, FIELD, SearchFilterOperator.IS_EQUAL, VALUE);
		String string1 = filter1.toString();
		String expected1 = String.format("%s%s%s%s%s%s", SearchFilterOperator.FILTER_AND, FIELD, d,
				SearchFilterOperator.IS_EQUAL.getSymbol(), d, VALUE);
		Assert.assertEquals(expected1, string1);
		Assert.assertEquals(filter1, SearchFilter.fromString(string1));
		SearchFilter filter2 = new SearchFilter(true, FIELD, SearchFilterOperator.IS_NOT_NULL);
		String string2 = filter2.toString();
		String expected2 = String.format("%s%s%s%s", SearchFilterOperator.FILTER_AND, FIELD, d,
				SearchFilterOperator.IS_NOT_NULL.getSymbol());
		Assert.assertEquals(expected2, string2);
		Assert.assertEquals(filter2, SearchFilter.fromString(string2));
	}

	@Test
	public void testSearchFilters() {
		// Bad order field
		try {
			SearchFilters.fromString(String.format("%s%s%s",
					new SearchFilter(true, FIELD, SearchFilterOperator.IS_EQUAL, VALUE),
					new SearchFilter(true, FIELD, SearchFilterOperator.IS_NOT_NULL), SearchFilterOperator.FILTER_OR));
		} catch (IllegalArgumentException e) {
			Assert.assertNotNull(e);
		}
		SearchFilters<Object> filters = SearchFilters.fromString(String.format("%s%s%s",
				new SearchFilter(true, FIELD, SearchFilterOperator.IS_EQUAL, VALUE), SearchFilterOperator.FILTER_OR,
				new SearchFilter(true, FIELD, SearchFilterOperator.IS_NOT_NULL)));
		Assert.assertNotNull(filters);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFromString_WithNull() {
		SearchFilter.fromString(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFromString_WithInvalidField() {
		SearchFilter.fromString("1");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSearchFilter_WithSingleOperator() {
		new SearchFilter(true, "alpha", SearchFilterOperator.IS_EQUAL);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSearchFilter_WithoutOperator() {
		SearchFilterOperator no = null;
		new SearchFilter(true, "alpha", no, null);
	}

	@Test
	public void testAndOr() {
		final SearchFilterOperator operator = SearchFilterOperator.IS_EQUAL;
		final String field = "field";
		final long value = 2;
		final SearchFilter andItemFilter = new SearchFilter(true, field, operator, value);
		final SearchFilter orItemFilter = new SearchFilter(false, field, operator, value);
		final SearchFilter[] filters1 = new SearchFilter[] { andItemFilter, orItemFilter, orItemFilter };
		final SearchFilter[] filters2 = new SearchFilter[] { andItemFilter, orItemFilter, andItemFilter };

		Assert.assertArrayEquals(new SearchFilter[] { andItemFilter, orItemFilter, orItemFilter, andItemFilter },
				SearchFilter.compute(andItemFilter, andItemFilter, orItemFilter, orItemFilter));

		// (A + B + C) * (D + E * F) = ((A & D) | (B & D) | (C & D) | (A & E &
		// F) | (B & E & F) | (C & E & F))
		Assert.assertNotNull(SearchFilter.concat(true, filters1, filters2));
		// (A + B + C) + (D + E * F) = A + B + C + D + E * F
		Assert.assertNotNull(SearchFilter.concat(false, filters2, filters1));

		// (A + B + C) * (D + E * F) = ((A & D) | (B & D) | (C & D) | (A & E &
		// F) | (B & E & F) | (C & E & F))
		Assert.assertArrayEquals(SearchFilter.filtersFromString(
				"-a,=,a;-d,=,d;|b,=,b;-d,=,d;|c,=,c;-d,=,d;|a,=,a;-e,=,e;-f,=,f;|b,=,b;-e,=,e;-f,=,f;|c,=,c;-e,=,e;-f,=,f"),
				SearchFilter.concat(true, SearchFilter.filtersFromString("-a,=,a;|b,=,b;|c,=,c"),
						SearchFilter.filtersFromString("-d,=,d;|e,=,e;-f,=,f")));
	}
}
