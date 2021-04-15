package com.gcorp.exception;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class ExceptionTest {

	@Test
	void testException() {
		assertNotNull(new StandardRuntimeException());
		assertNotNull(new StandardRuntimeException(new Exception()));
		assertNotNull(new StandardRuntimeException("message", new Exception()));
	}
}
