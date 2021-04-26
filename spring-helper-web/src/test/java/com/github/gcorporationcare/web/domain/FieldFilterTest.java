package com.github.gcorporationcare.web.domain;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import com.github.gcorporationcare.data.common.Utils;
import com.github.gcorporationcare.notest.common.RandomUtils;
import com.github.gcorporationcare.notest.dto.PersonDto;
import com.github.gcorporationcare.notest.entity.Person;

class FieldFilterTest {
	ModelMapper modelMapper;

	@BeforeEach
	void setUp() {
		modelMapper = new ModelMapper();
	}

	@Test
	void testSetAllIfEmpty() {
		final String fields = "fields";
		FieldFilter<PersonDto> filter = FieldFilter.fromString(null);
		assertNull(Utils.getFieldValue(fields, filter, FieldFilter.class));
		filter.setToAllIfEmpty();
		String[] fieldsValue = (String[]) Utils.getFieldValue(fields, filter, FieldFilter.class);
		assertArrayEquals(new String[] { FieldFilter.ALL_FIELDS }, fieldsValue);

		FieldFilter<PersonDto> nonEmptyFilter = FieldFilter.fromString("name");
		nonEmptyFilter.setToAllIfEmpty();
		String[] nonEmptyFieldsValue = (String[]) Utils.getFieldValue(fields, nonEmptyFilter, FieldFilter.class);
		assertFalse(Arrays.stream(nonEmptyFieldsValue).anyMatch(FieldFilter.ALL_FIELDS::equals));
	}

	@Test
	void testReadDefaultFields() {
		Person person = RandomUtils.randomPerson();
		PersonDto personDto = modelMapper.map(person, PersonDto.class);
		PersonDto defaultPerson = FieldFilter.<PersonDto>fromString(null).parseEntity(personDto);
		assertNotNull(defaultPerson.getEmail());
		assertNull(defaultPerson.getLanguage());

		PersonDto defaultPlusPerson = FieldFilter.<PersonDto>fromString("+language").parseEntity(personDto);
		// No more null since it has been added
		assertNotNull(defaultPlusPerson.getLanguage());
		assertNotNull(defaultPlusPerson.getEmail());
	}

	@Test
	void testParseIterable() {
		List<PersonDto> personsDto = new ArrayList<>();
		List<Person> persons = IntStream.range(0, 10).mapToObj(i -> {
			Person person = new Person();
			person.setEmail(RandomUtils.randomEmail());
			person.setName(String.format("person-%d", i));
			personsDto.add(modelMapper.map(person, PersonDto.class));
			return person;
		}).collect(Collectors.toList());
		FieldFilter<PersonDto> fieldFilter = FieldFilter.fromString("name,email");
		assertEquals(persons.size(), ((List<PersonDto>) fieldFilter.parseIterable(personsDto)).size());

		fieldFilter = FieldFilter.fromString(null);
		assertEquals(persons.size(), ((List<PersonDto>) fieldFilter.parseIterable(personsDto)).size());

		fieldFilter.setToAllIfEmpty();
		assertEquals(persons.size(), ((List<PersonDto>) fieldFilter.parseIterable(personsDto)).size());
	}

	@Test
	void testReadCustomFields() {
		Person person = new Person();
		person.setEmail(RandomUtils.randomEmail());
		person.setLanguage("alpha");
		person.setName("Alphabet");
		PersonDto personDto = modelMapper.map(person, PersonDto.class);
		// Even with typo, name is a minimal field (never omitted)
		PersonDto customPerson = FieldFilter.<PersonDto>fromString("languaage,naame").parseEntity(personDto);
		// Email is minimal field so will never be omitted
		assertNotNull(customPerson.getEmail());
		assertNotNull(customPerson.getName());
		assertNull(customPerson.getLanguage());
	}
}
