package com.gcorp.entity;

import java.util.Locale;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.gcorp.ApiStarter;
import com.gcorp.notest.common.RandomUtils;
import com.gcorp.notest.config.H2Config;
import com.gcorp.notest.entity.Promotion;
import com.gcorp.notest.entity.PromotionTranslation;
import com.gcorp.notest.repository.PromotionRepository;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ApiStarter.class, H2Config.class })
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BaseTranslatableEntityTest {

	@Autowired
	PromotionRepository promotionRepository;

	@Test
	public void testCreate() {
		Promotion promotion = RandomUtils.randomPromotion();
		Assert.assertTrue(promotion.getTranslations().isEmpty());
		promotion = promotionRepository.save(promotion);
		Assert.assertFalse(promotion.getTranslations().isEmpty());
		PromotionTranslation translation = promotion.getTranslation(promotion.getLanguage());
		Assert.assertEquals(promotion.getName(), translation.getName());
		Assert.assertEquals(promotion.getDescription(), translation.getDescription());
	}

	@Test
	public void testUpdate() {
		final String englishName = "New name";
		final String frenchName = "Nouveau nom";
		Promotion promotion = promotionRepository.save(RandomUtils.randomPromotion());
		Assert.assertNotEquals(englishName, promotion.getName());
		promotion.setName("New name");
		promotion = promotionRepository.save(promotion);
		PromotionTranslation translation = promotion.getTranslation(promotion.getLanguage());
		Assert.assertEquals(englishName, translation.getName());
		promotion.setLanguage(Locale.FRENCH.getLanguage());
		promotion.setName(frenchName);
		promotion = promotionRepository.save(promotion);
		Assert.assertEquals(englishName, translation.getName());
		Assert.assertEquals(frenchName, promotion.getTranslation(Locale.FRENCH.getLanguage()).getName());
	}

	@Test
	public void testLoad() {
		final String englishDescription = "My description";
		final String frenchDescription = "Ma description";
		Promotion promotion = RandomUtils.randomPromotion();

		promotion.setDescription(frenchDescription);
		PromotionTranslation french = RandomUtils.randomPromotionTranslation(promotion);
		french.setLanguage(Locale.FRENCH.getLanguage());
		promotion.getTranslations().add(french);

		promotion.setDescription(englishDescription);
		PromotionTranslation english = RandomUtils.randomPromotionTranslation(promotion);
		english.setLanguage(Locale.ENGLISH.getLanguage());
		promotion.getTranslations().add(english);

		promotion = promotionRepository.save(promotion);

		Promotion retrieved = promotionRepository.findById(promotion.getId()).get();
		Assert.assertEquals(englishDescription, retrieved.getDescription());
		retrieved.setLanguage(Locale.FRENCH.getLanguage());
		retrieved.load();
		Assert.assertEquals(frenchDescription, retrieved.getDescription());
	}
}
