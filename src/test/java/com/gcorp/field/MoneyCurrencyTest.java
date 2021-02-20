package com.gcorp.field;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.gcorp.common.Utils;

public class MoneyCurrencyTest {

	@Test
	public void testListCurrencies() {
		List<MoneyCurrency> englishCurrencies = MoneyCurrency.listCurrencies(Utils.DEFAULT_LOCALE.getLanguage());
		List<MoneyCurrency> frenchCurrencies = MoneyCurrency.listCurrencies("fr");
		Assert.assertEquals(englishCurrencies.size(), frenchCurrencies.size());
		Assert.assertTrue(
				englishCurrencies.stream().filter(c -> "XOF".equalsIgnoreCase(c.getCode())).findFirst().isPresent());
	}
	
	@Test
	public void testFind() {
		Assert.assertNotNull(MoneyCurrency.find("gBp"));
		Assert.assertNotNull(MoneyCurrency.find("GBP"));
		Assert.assertNull(MoneyCurrency.find("GB"));
	}
}
