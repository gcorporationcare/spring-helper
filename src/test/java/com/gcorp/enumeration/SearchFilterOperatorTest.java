package com.gcorp.enumeration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.gcorp.domain.SearchFilter.SearchFilterOperator;

class SearchFilterOperatorTest {

	@Test
	void testGet() {
		for (SearchFilterOperator operator : SearchFilterOperator.values()) {
			assertEquals(operator, SearchFilterOperator.get(operator.getSymbol()));
		}
	}

	@Test
	void testGet_WithInvalid() {
		// Inexistent
		assertThrows(IllegalArgumentException.class, () -> SearchFilterOperator.get("%*"));
	}

	void testNoDuplicate() {
		List<String> symbols = new ArrayList<>();
		Arrays.stream(SearchFilterOperator.values()).forEach(o -> {
			fail(String.format("Duplicate found for %s with sign %s", o, o.getSymbol()));
			symbols.add(o.getSymbol());
		});
		assertEquals(SearchFilterOperator.values().length, symbols.size());
	}
}
