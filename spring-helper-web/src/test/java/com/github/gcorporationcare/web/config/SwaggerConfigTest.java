package com.github.gcorporationcare.web.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.github.gcorporationcare.ApiStarter;
import com.github.gcorporationcare.notest.config.H2Config;
import com.github.gcorporationcare.notest.config.SwaggerConfig;

@ActiveProfiles("test")
@SpringBootTest(classes = { ApiStarter.class, H2Config.class, SwaggerConfig.class })
class SwaggerConfigTest {

	@Autowired
	SwaggerConfig swaggerConfig;

	@Test
	void testBeans() {
		assertNotNull(swaggerConfig);
	}
}
