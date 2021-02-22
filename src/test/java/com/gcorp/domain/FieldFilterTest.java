package com.gcorp.domain;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Test;

import com.gcorp.common.Utils;
import com.gcorp.notest.common.RandomUtils;
import com.gcorp.notest.entity.Person;

public class FieldFilterTest {

	@Test
	public void testSetAllIfEmpty() {
		final String fields = "fields";
		FieldFilter<Person> filter = FieldFilter.fromString(null);
		Assert.assertNull(Utils.getFieldValue(fields, filter, FieldFilter.class));
		filter.setToAllIfEmpty();
		String[] fieldsValue = (String[]) Utils.getFieldValue(fields, filter, FieldFilter.class);
		Assert.assertArrayEquals(new String[] { FieldFilter.ALL_FIELDS }, fieldsValue);

		FieldFilter<Person> nonEmptyFilter = FieldFilter.fromString("name");
		nonEmptyFilter.setToAllIfEmpty();
		String[] nonEmptyFieldsValue = (String[]) Utils.getFieldValue(fields, nonEmptyFilter, FieldFilter.class);
		Assert.assertFalse(Arrays.stream(nonEmptyFieldsValue).anyMatch(FieldFilter.ALL_FIELDS::equals));
	}

	@Test
	public void testReadDefaultFields() {
		Person person = new Person("Any name", RandomUtils.randomEmail(), "en", new int[] { 1, 2 },
				new Double[] { 1.2, 2.3 }, new String[] { "add", "sub" }, new HashSet<>());
		Person defaultPerson = FieldFilter.<Person>fromString(null).parseEntity(person);
		Assert.assertNotNull(defaultPerson.getEmail());
		Assert.assertNull(defaultPerson.getLanguage());

		Person defaultPlusPerson = FieldFilter.<Person>fromString("+language").parseEntity(person);
		// No more null since it has been added
		Assert.assertNotNull(defaultPlusPerson.getLanguage());
		Assert.assertNotNull(defaultPlusPerson.getEmail());
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
		Assert.assertEquals(persons.size(), ((List<Person>) fieldFilter.parseIterable(persons)).size());

		fieldFilter = FieldFilter.fromString(null);
		Assert.assertEquals(persons.size(), ((List<Person>) fieldFilter.parseIterable(persons)).size());

		fieldFilter.setToAllIfEmpty();
		Assert.assertEquals(persons.size(), ((List<Person>) fieldFilter.parseIterable(persons)).size());
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
		Assert.assertNotNull(customPerson.getEmail());
		Assert.assertNotNull(customPerson.getName());
		Assert.assertNull(customPerson.getLanguage());
	}
}
