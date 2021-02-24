package com.gcorp.common;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.gcorp.ApiStarter;
import com.gcorp.exception.RequestException;
import com.gcorp.exception.ValidationException;
import com.gcorp.notest.config.H2Config;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ApiStarter.class, H2Config.class })
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ApiResponseTest {

	@Test
	public void testApiResponse() {
		Assert.assertNotNull(new ApiResponse<>(HttpStatus.ACCEPTED));
		Assert.assertNotNull(new ApiResponse<>(true, HttpStatus.BAD_GATEWAY));
		Assert.assertNotNull(new ApiResponse<>(new RequestException("simple")));
		Assert.assertNotNull(new ApiResponse<>(new ValidationException("another")));
		ApiResponse<String> response = new ApiResponse<>("response", HttpStatus.OK);
		Assert.assertEquals(response, response);
	}
}
