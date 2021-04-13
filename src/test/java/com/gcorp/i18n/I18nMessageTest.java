package com.gcorp.i18n;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import com.gcorp.ApiStarter;
import com.gcorp.notest.config.H2Config;

@ActiveProfiles("test")
@SpringBootTest(classes = { ApiStarter.class, H2Config.class })
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
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
		assertNotNull(messageKey.getMessage(KEY));
		assertEquals(MESSAGE_FR, messageKey.getMessage(KEY, localeFr));
		assertEquals(MESSAGE_EN, messageKey.getMessage(KEY, localeEn));
	}
}
