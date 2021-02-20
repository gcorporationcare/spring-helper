package com.gcorp.entity;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class BaseEntityTest {
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	class Person extends BaseEntity {
		private static final long serialVersionUID = 1L;
		private String name;
		private String lastName;
		private int[] numbers;
		private String[] addresses;
		private Double[] floats;
		private Set<Person> booleans = new HashSet<>();

		@Override
		public void format() {
			// Nothing
		}
	}

	@Test
	public void testCopy_WithNothing() {
		Person origin = new Person();
		origin.setName("name");
		origin.setLastName("lastName");
		origin.setAddresses(new String[] { "1", "2" });
		origin.setFloats(new Double[] { 1.2, 2.3, 3.4 });
		origin.setNumbers(new int[] { 1, 4, 5 });
		origin.getBooleans().add(new Person());
		origin.getBooleans().add(new Person());
		Person target = new Person();
		target.merge(origin, null);
		Assert.assertEquals(origin.getName(), target.getName());
		Assert.assertEquals(origin.getLastName(), target.getLastName());
		Assert.assertArrayEquals(origin.getAddresses(), target.getAddresses());
		Assert.assertArrayEquals(origin.getNumbers(), target.getNumbers());
		Assert.assertArrayEquals(origin.getFloats(), target.getFloats());
	}

}
