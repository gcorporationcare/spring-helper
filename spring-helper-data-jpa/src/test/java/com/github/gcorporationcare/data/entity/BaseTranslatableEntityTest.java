package com.github.gcorporationcare.data.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import com.github.gcorporationcare.ApiStarter;
import com.github.gcorporationcare.notest.common.RandomUtils;
import com.github.gcorporationcare.notest.config.H2Config;
import com.github.gcorporationcare.notest.entity.Promotion;
import com.github.gcorporationcare.notest.entity.PromotionTranslation;
import com.github.gcorporationcare.notest.repository.PromotionRepository;

@ActiveProfiles("test")
@SpringBootTest(classes = { ApiStarter.class, H2Config.class })
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class BaseTranslatableEntityTest {

	@Autowired
	PromotionRepository promotionRepository;

	@Test
	void testCreate() {
		Promotion promotion = RandomUtils.randomPromotion();
		assertTrue(promotion.getTranslations().isEmpty());
		promotion = promotionRepository.save(promotion);
		assertFalse(promotion.getTranslations().isEmpty());
		PromotionTranslation translation = promotion.getTranslation(promotion.getLanguage());
		assertEquals(promotion.getName(), translation.getName());
		assertEquals(promotion.getDescription(), translation.getDescription());
	}

	@Test
	void testUpdate() {
		final String englishName = "New name";
		final String frenchName = "Nouveau nom";
		Promotion promotion = promotionRepository.save(RandomUtils.randomPromotion());
		assertNotEquals(englishName, promotion.getName());
		promotion.setName("New name");
		promotion = promotionRepository.save(promotion);
		PromotionTranslation translation = promotion.getTranslation(promotion.getLanguage());
		assertEquals(englishName, translation.getName());
		promotion.setLanguage(Locale.FRENCH.getLanguage());
		promotion.setName(frenchName);
		promotion = promotionRepository.save(promotion);
		assertEquals(englishName, translation.getName());
		assertEquals(frenchName, promotion.getTranslation(Locale.FRENCH.getLanguage()).getName());
	}

	@Test
	void testLoad() {
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
		assertEquals(englishDescription, retrieved.getDescription());
		retrieved.setLanguage(Locale.FRENCH.getLanguage());
		retrieved.load();
		assertEquals(frenchDescription, retrieved.getDescription());
	}
}
