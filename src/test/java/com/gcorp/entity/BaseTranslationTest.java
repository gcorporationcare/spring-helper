package com.gcorp.entity;

import org.junit.Assert;
import org.junit.Test;

import com.gcorp.notest.common.RandomUtils;
import com.gcorp.notest.entity.PromotionTranslation;

public class BaseTranslationTest {

	@Test
	public void testFormat_OK() {
		PromotionTranslation translation = RandomUtils.randomPromotionTranslation(null);
		translation.format();
		Assert.assertTrue(translation.translated());
		translation.setDescription(null);
		Assert.assertFalse(translation.translated());
	}
}
