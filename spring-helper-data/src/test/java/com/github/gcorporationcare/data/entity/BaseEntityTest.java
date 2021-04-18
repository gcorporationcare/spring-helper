package com.github.gcorporationcare.data.entity;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

class BaseEntityTest {
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
	void testCopy_WithNothing() {
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
		assertEquals(origin.getName(), target.getName());
		assertEquals(origin.getLastName(), target.getLastName());
		assertArrayEquals(origin.getAddresses(), target.getAddresses());
		assertArrayEquals(origin.getNumbers(), target.getNumbers());
		assertArrayEquals(origin.getFloats(), target.getFloats());
	}

}
