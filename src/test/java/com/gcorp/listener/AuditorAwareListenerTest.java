package com.gcorp.listener;

import org.junit.Assert;
import org.junit.Test;

public class AuditorAwareListenerTest {

	@Test
	public void testGetCurrentAuditor() {
		AuditorAwareListener listener = new AuditorAwareListener();
		Assert.assertTrue(listener.getCurrentAuditor().isPresent());
		Assert.assertNotNull(listener.getCurrentAuditor().get());
	}
}
