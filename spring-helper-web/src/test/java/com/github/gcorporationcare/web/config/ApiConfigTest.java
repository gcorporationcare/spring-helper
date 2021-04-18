package com.github.gcorporationcare.web.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import com.github.gcorporationcare.ApiStarter;
import com.github.gcorporationcare.notest.config.H2Config;
import com.github.gcorporationcare.web.common.RequestIdGenerator;

@ActiveProfiles("test")
@SpringBootTest(classes = { ApiStarter.class, H2Config.class })
class ApiConfigTest {

	@Autowired
	ApiConfig apiConfig;
	@Autowired
	RequestIdGenerator requestIdGenerator;
	@Autowired
	LocaleChangeInterceptor localeChangeInterceptor;
	@Autowired
	LocaleResolver localeResolver;

	@Test
	void testBeans() {
		assertNotNull(apiConfig);
		assertNotNull(apiConfig.requestIdHeaderName());
		assertNotNull(localeResolver);
		assertNotNull(localeChangeInterceptor);
		assertNotNull(requestIdGenerator);
	}
}
