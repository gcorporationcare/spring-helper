package com.gcorp.common;

import org.junit.Assert;
import org.junit.Test;

public class UtilsTest {

	@Test
	public void testGetProperNoun() {
		final String expected = "John Doe";
		Assert.assertEquals(expected, Utils.getProperNoun(expected));
		Assert.assertEquals(expected, Utils.getProperNoun("john doe"));
		Assert.assertEquals(expected, Utils.getProperNoun("jOhn DoE"));
		Assert.assertEquals("John N'Aidoe", Utils.getProperNoun("jOhn n'aiDoE"));
		Assert.assertEquals("John-N'Ai'Doe", Utils.getProperNoun("jOhn-N'ai'DoE"));
	}
}
