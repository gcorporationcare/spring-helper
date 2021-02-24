package com.gcorp.exception;

import org.junit.Assert;
import org.junit.Test;

public class ExceptionTest {

	@Test
	public void testException() {
		Assert.assertNotNull(new StandardRuntimeException());
		Assert.assertNotNull(new StandardRuntimeException(new Exception()));
		Assert.assertNotNull(new StandardRuntimeException("message", new Exception()));
	}
}
