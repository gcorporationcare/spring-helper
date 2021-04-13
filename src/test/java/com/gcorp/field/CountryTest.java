package com.gcorp.field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.gcorp.common.Utils;

public class CountryTest {

	@Test
	public void testEqualsAndHashCode() {
		Country us = Country.find("us");
		Country fr = Country.find("fr");
		assertNotEquals(us, fr);
		assertNotEquals(us.hashCode(), fr.hashCode());
		assertEquals(us, Country.find("uS"));
	}

	@Test
	public void testList() {
		List<Country> englishCountries = Country.listCountries(Utils.DEFAULT_LOCALE.getLanguage());
		List<Country> frenchCountries = Country.listCountries("fr");
		assertEquals(englishCountries.size(), frenchCountries.size());
		assertTrue(
				englishCountries.stream().filter(c -> "CI".equalsIgnoreCase(c.getCode())).findFirst().isPresent());
	}

	@Test
	public void testFind() {
		assertNotNull(Country.find("us"));
		assertNotNull(Country.find("US"));
		assertNull(Country.find("usa"));
	}
}
