package com.gcorp.field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.gcorp.common.Utils;

class MoneyCurrencyTest {

	@Test
	void testEqualsAndHashCode() {
		MoneyCurrency usd = MoneyCurrency.find("usd");
		MoneyCurrency eur = MoneyCurrency.find("eur");
		assertNotEquals(usd, eur);
		assertNotEquals(usd.hashCode(), eur.hashCode());
		assertEquals(usd, MoneyCurrency.find("uSD"));
	}

	@Test
	void testListCurrencies() {
		List<MoneyCurrency> englishCurrencies = MoneyCurrency.listCurrencies(Utils.DEFAULT_LOCALE.getLanguage());
		List<MoneyCurrency> frenchCurrencies = MoneyCurrency.listCurrencies("fr");
		assertEquals(englishCurrencies.size(), frenchCurrencies.size());
		assertTrue(englishCurrencies.stream().filter(c -> "XOF".equalsIgnoreCase(c.getCode())).findFirst().isPresent());
	}

	@Test
	void testFind() {
		MoneyCurrency currency = MoneyCurrency.find("gBp");
		assertNotNull(currency);
		assertNotNull(currency.getCode());
		assertNotNull(currency.getSymbol());
		assertNotNull(currency.getName());
		assertNotNull(currency.getDisplayName());
		assertNotNull(MoneyCurrency.find("GBP"));
		assertNull(MoneyCurrency.find("GB"));
	}
}
