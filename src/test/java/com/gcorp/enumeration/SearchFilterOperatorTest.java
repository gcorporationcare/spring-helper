package com.gcorp.enumeration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.gcorp.domain.SearchFilter.SearchFilterOperator;

public class SearchFilterOperatorTest {

	@Test
	public void testGet() {
		for (SearchFilterOperator operator : SearchFilterOperator.values()) {
			Assert.assertEquals(operator, SearchFilterOperator.get(operator.getSymbol()));
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void tesGet_WithInvalid() {
		SearchFilterOperator.get("%*"); // Inexistent
	}

	public void testNoDuplicate() {
		List<String> symbols = new ArrayList<>();
		Arrays.stream(SearchFilterOperator.values()).forEach(o -> {
			Assert.fail(String.format("Duplicate found for %s with sign %s", o, o.getSymbol()));
			symbols.add(o.getSymbol());
		});
		Assert.assertEquals(SearchFilterOperator.values().length, symbols.size());
	}
}
