package com.github.gcorporationcare.data.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.AuditorAware;
import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.gcorporationcare.ApiStarter;
import com.github.gcorporationcare.data.i18n.I18nMessage;
import com.github.gcorporationcare.notest.config.H2Config;

@SpringBootTest(classes = { ApiStarter.class, H2Config.class })
@ActiveProfiles("Test")
class DataConfigTest {

	@Autowired
	AuditorAware<String> auditorAware;
	@Autowired
	ResourceBundleMessageSource resourceBundleMessageSource;
	@Autowired
	I18nMessage i18nMessage;
	@Autowired
	Module javaTimeModule;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	DataConfig dataConfig;

	@Test
	void testBeans() {
		assertNotNull(auditorAware);
		assertNotNull(resourceBundleMessageSource);
		assertNotNull(i18nMessage);
		assertNotNull(javaTimeModule);
		assertNotNull(objectMapper);
		assertNotNull(dataConfig.i18nMessageSourceLocation());
		assertNotNull(dataConfig.auditorAwareListener);
	}
}
