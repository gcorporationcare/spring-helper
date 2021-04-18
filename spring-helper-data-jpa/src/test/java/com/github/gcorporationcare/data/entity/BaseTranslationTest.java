package com.github.gcorporationcare.data.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.github.gcorporationcare.notest.common.RandomUtils;
import com.github.gcorporationcare.notest.entity.PromotionTranslation;

class BaseTranslationTest {

	@Test
	void testFormat_OK() {
		PromotionTranslation translation = RandomUtils.randomPromotionTranslation(null);
		translation.format();
		assertTrue(translation.translated());
		translation.setDescription(null);
		assertFalse(translation.translated());
	}

	@Test
	void testFormat_KO() {
		PromotionTranslation translation = RandomUtils.randomPromotionTranslation(null);
		translation.setLanguage("AnY");
		translation.format();
		assertEquals("any", translation.getLanguage());
	}
}
