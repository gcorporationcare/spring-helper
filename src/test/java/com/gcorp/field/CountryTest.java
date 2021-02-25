package com.gcorp.field;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.gcorp.common.Utils;

public class CountryTest {

	@Test
	public void testEqualsAndHashCode() {
		Country us = Country.find("us");
		Country fr = Country.find("fr");
		Assert.assertNotEquals(us, fr);
		Assert.assertNotEquals(us.hashCode(), fr.hashCode());
		Assert.assertEquals(us, Country.find("uS"));
	}

	@Test
	public void testList() {
		List<Country> englishCountries = Country.listCountries(Utils.DEFAULT_LOCALE.getLanguage());
		List<Country> frenchCountries = Country.listCountries("fr");
		Assert.assertEquals(englishCountries.size(), frenchCountries.size());
		Assert.assertTrue(
				englishCountries.stream().filter(c -> "CI".equalsIgnoreCase(c.getCode())).findFirst().isPresent());
	}

	@Test
	public void testFind() {
		Assert.assertNotNull(Country.find("us"));
		Assert.assertNotNull(Country.find("US"));
		Assert.assertNull(Country.find("usa"));
	}
}
