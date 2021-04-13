package com.gcorp.listener;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class AuditorAwareListenerTest {

	@Test
	public void testGetCurrentAuditor() {
		AuditorAwareListener listener = new AuditorAwareListener();
		assertTrue(listener.getCurrentAuditor().isPresent());
		assertNotNull(listener.getCurrentAuditor().get());
	}
}
