package com.gcorp.i18n;

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
import com.gcorp.notest.config.H2Config;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ApiStarter.class, H2Config.class })
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class I18nMessageTest {

	private final String KEY = "api.greeting";
	private final String MESSAGE_EN = "Welcome to life";
	private final String MESSAGE_FR = "Bienvenue dans la vie";

	@Autowired
	I18nMessage messageKey;

	@Test
	public void testGetMessage() {
		Locale localeEn = Locale.ENGLISH;
		Locale localeFr = Locale.FRENCH;
		Assert.assertNotNull(messageKey.getMessage(KEY));
		Assert.assertEquals(MESSAGE_FR, messageKey.getMessage(KEY, localeFr));
		Assert.assertEquals(MESSAGE_EN, messageKey.getMessage(KEY, localeEn));
	}
}
