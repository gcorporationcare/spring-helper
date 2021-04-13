package com.gcorp.domain;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import com.gcorp.common.Utils;
import com.gcorp.notest.common.RandomUtils;
import com.gcorp.notest.entity.Person;

public class FieldFilterTest {

	@Test
	public void testSetAllIfEmpty() {
		final String fields = "fields";
		FieldFilter<Person> filter = FieldFilter.fromString(null);
		assertNull(Utils.getFieldValue(fields, filter, FieldFilter.class));
		filter.setToAllIfEmpty();
		String[] fieldsValue = (String[]) Utils.getFieldValue(fields, filter, FieldFilter.class);
		assertArrayEquals(new String[] { FieldFilter.ALL_FIELDS }, fieldsValue);

		FieldFilter<Person> nonEmptyFilter = FieldFilter.fromString("name");
		nonEmptyFilter.setToAllIfEmpty();
		String[] nonEmptyFieldsValue = (String[]) Utils.getFieldValue(fields, nonEmptyFilter, FieldFilter.class);
		assertFalse(Arrays.stream(nonEmptyFieldsValue).anyMatch(FieldFilter.ALL_FIELDS::equals));
	}

	@Test
	public void testReadDefaultFields() {
		Person person = RandomUtils.randomPerson();
		Person defaultPerson = FieldFilter.<Person>fromString(null).parseEntity(person);
		assertNotNull(defaultPerson.getEmail());
		assertNull(defaultPerson.getLanguage());

		Person defaultPlusPerson = FieldFilter.<Person>fromString("+language").parseEntity(person);
		// No more null since it has been added
		assertNotNull(defaultPlusPerson.getLanguage());
		assertNotNull(defaultPlusPerson.getEmail());
	}

	@Test
	public void testParseIterable() {
		List<Person> persons = IntStream.range(0, 10).mapToObj(i -> {
			Person person = new Person();
			person.setEmail(RandomUtils.randomEmail());
			person.setName(String.format("person-%d", i));
			return person;
		}).collect(Collectors.toList());
		FieldFilter<Person> fieldFilter = FieldFilter.fromString("name,email");
		assertEquals(persons.size(), ((List<Person>) fieldFilter.parseIterable(persons)).size());

		fieldFilter = FieldFilter.fromString(null);
		assertEquals(persons.size(), ((List<Person>) fieldFilter.parseIterable(persons)).size());

		fieldFilter.setToAllIfEmpty();
		assertEquals(persons.size(), ((List<Person>) fieldFilter.parseIterable(persons)).size());
	}

	@Test
	public void testReadCustomFields() {
		Person person = new Person();
		person.setEmail(RandomUtils.randomEmail());
		person.setLanguage("alpha");
		person.setName("Alphabet");
		// Even with typo, name is a minimal field (never omitted)
		Person customPerson = FieldFilter.<Person>fromString("languaage,naame").parseEntity(person);
		// Email is minimal field so will never be omitted
		assertNotNull(customPerson.getEmail());
		assertNotNull(customPerson.getName());
		assertNull(customPerson.getLanguage());
	}
}
