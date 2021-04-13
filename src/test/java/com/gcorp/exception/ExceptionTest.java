package com.gcorp.exception;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class ExceptionTest {

	@Test
	public void testException() {
		assertNotNull(new StandardRuntimeException());
		assertNotNull(new StandardRuntimeException(new Exception()));
		assertNotNull(new StandardRuntimeException("message", new Exception()));
	}
}
