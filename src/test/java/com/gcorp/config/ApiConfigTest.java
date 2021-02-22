package com.gcorp.config;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.AuditorAware;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.servlet.LocaleResolver;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gcorp.ApiStarter;
import com.gcorp.i18n.I18nMessage;
import com.gcorp.notest.config.H2Config;

@SpringBootTest(classes = { ApiStarter.class, H2Config.class })
@ActiveProfiles("Test")
@RunWith(SpringRunner.class)
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
		Assert.assertNotNull(auditorAware);
		Assert.assertNotNull(localeResolver);
		Assert.assertNotNull(resourceBundleMessageSource);
		Assert.assertNotNull(i18nMessage);
		Assert.assertNotNull(javaTimeModule);
		Assert.assertNotNull(objectMapper);
	}
}
