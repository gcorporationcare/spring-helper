package com.gcorp.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.AuditorAware;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.servlet.LocaleResolver;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gcorp.ApiStarter;
import com.gcorp.i18n.I18nMessage;
import com.gcorp.notest.config.H2Config;

@SpringBootTest(classes = { ApiStarter.class, H2Config.class })
@ActiveProfiles("Test")
public class ApiConfigTest {

	@Autowired
	AuditorAware<String> auditorAware;
	@Autowired
	LocaleResolver localeResolver;
	@Autowired
	ResourceBundleMessageSource resourceBundleMessageSource;
	@Autowired
	I18nMessage i18nMessage;
	@Autowired
	Module javaTimeModule;
	@Autowired
	ObjectMapper objectMapper;

	@Test
	public void testBeans() {
		assertNotNull(auditorAware);
		assertNotNull(localeResolver);
		assertNotNull(resourceBundleMessageSource);
		assertNotNull(i18nMessage);
		assertNotNull(javaTimeModule);
		assertNotNull(objectMapper);
	}
}
