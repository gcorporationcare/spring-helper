package com.github.gcorporationcare.web.common;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import com.github.gcorporationcare.ApiStarter;
import com.github.gcorporationcare.data.exception.ValidationException;
import com.github.gcorporationcare.notest.config.H2Config;
import com.github.gcorporationcare.notest.entity.Person;
import com.github.gcorporationcare.web.exception.RequestException;

@ActiveProfiles("test")
@SpringBootTest(classes = { ApiStarter.class, H2Config.class })
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class ApiResponseTest {

	// -------------------------------------------------
	private static ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
	// -------------------------------------------------
	private static Validator validator = validatorFactory.getValidator();

	@Test
	void testConstructors() {
		assertNotNull(new ApiResponse<>(HttpStatus.NO_CONTENT));
		assertNotNull(new ApiResponse<>("alphabet", HttpStatus.BAD_GATEWAY));
		assertNotNull(new ApiResponse<>(new IllegalArgumentException("Nothing"), HttpStatus.BAD_REQUEST, true,
				"not true", 1L));
		assertNotNull(new ApiResponse<>(new RequestException("Some test")));
		assertNotNull(new ApiResponse<>(new RequestException("Great test", HttpStatus.GONE), HttpStatus.GONE));
		Set<ConstraintViolation<Object>> violations = validator.validate(new Person());
		assertNotNull(new ApiResponse<>(new ValidationException("invalid", violations)));
	}

	@Test
	void testExceptionToMap() {
		assertNotNull(ApiResponse.exceptionToMap(new IllegalArgumentException("attempt"), null, 1, 2, 3));
		assertNotNull(ApiResponse.exceptionToMap(new IllegalArgumentException("two"), HttpStatus.INTERNAL_SERVER_ERROR,
				new Object[0]));
	}
}
