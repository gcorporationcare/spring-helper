package com.gcorp.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import com.gcorp.ApiStarter;
import com.gcorp.exception.RequestException;
import com.gcorp.exception.ValidationException;
import com.gcorp.notest.config.H2Config;

@ActiveProfiles("test")
@SpringBootTest(classes = { ApiStarter.class, H2Config.class })
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class ApiResponseTest {

	@Test
	public void testApiResponse() {
		assertNotNull(new ApiResponse<>(HttpStatus.ACCEPTED));
		assertNotNull(new ApiResponse<>(true, HttpStatus.BAD_GATEWAY));
		assertNotNull(new ApiResponse<>(new RequestException("simple")));
		assertNotNull(new ApiResponse<>(new ValidationException("another")));
		final String body = "response";
		ApiResponse<String> response = new ApiResponse<>(body, HttpStatus.OK);
		ApiResponse<String> anotherResponse = new ApiResponse<>(body, HttpStatus.OK);
		assertEquals(response, anotherResponse);
	}
}
