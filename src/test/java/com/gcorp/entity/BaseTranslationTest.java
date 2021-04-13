package com.gcorp.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.gcorp.notest.common.RandomUtils;
import com.gcorp.notest.entity.PromotionTranslation;

public class BaseTranslationTest {

	@Test
	public void testFormat_OK() {
		PromotionTranslation translation = RandomUtils.randomPromotionTranslation(null);
		translation.format();
		assertTrue(translation.translated());
		translation.setDescription(null);
		assertFalse(translation.translated());
	}

	@Test
	public void testFormat_KO() {
		PromotionTranslation translation = RandomUtils.randomPromotionTranslation(null);
		translation.setLanguage("AnY");
		translation.format();
		assertEquals("any", translation.getLanguage());
	}
}
