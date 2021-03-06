package com.github.gcorporationcare.web.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.hibernate.query.criteria.internal.BasicPathUsageException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.TransactionSystemException;

import com.github.gcorporationcare.ApiStarter;
import com.github.gcorporationcare.data.domain.SearchFilter.SearchFilterOperator;
import com.github.gcorporationcare.data.domain.SearchFilters;
import com.github.gcorporationcare.notest.common.RandomUtils;
import com.github.gcorporationcare.notest.config.H2Config;
import com.github.gcorporationcare.notest.entity.Person;
import com.github.gcorporationcare.notest.repository.PersonRepository;
import com.github.gcorporationcare.notest.service.PersonService;
import com.github.gcorporationcare.web.exception.RequestException;

@ActiveProfiles("test")
@SpringBootTest(classes = { ApiStarter.class, H2Config.class })
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class BaseRegistrableServiceTest {

	@Autowired
	PersonService personService;
	@Autowired
	PersonRepository personRepository;

	@Test
	void testRead_OK() {
		Person person = personRepository.save(RandomUtils.randomPerson());
		Person personExist = personService.read(person.getId());
		assertEquals(person.getId(), personExist.getId());
		assertEquals(person.getEmail(), personExist.getEmail());
		assertEquals(person.getName(), personExist.getName());
		assertEquals(person.getLanguage(), personExist.getLanguage());
	}

	@Test
	void testRead_KO() {
		assertThrows(RequestException.class, () -> personService.read(0L));
	}

	@Test
	void testReadMultiple_OK() {
		final String language = "mydeepestlanguage";
		List<Person> persons = IntStream.range(0, 10).mapToObj(i -> {
			Person person = RandomUtils.randomPerson();
			if (i % 2 == 0) {
				person.setLanguage(language);
			}
			return person;
		}).collect(Collectors.toList());
		personRepository.saveAll(persons);
		SearchFilters<Person> filters = SearchFilters.of("language", SearchFilterOperator.IS_EQUAL, language);
		Page<Person> personsPage = personService.readMultiple(filters, null);
		assertEquals(5, personsPage.getTotalElements());
	}

	@Test
	void testReadMultiple_KO() {
		Person person = personRepository.save(RandomUtils.randomPerson());
		SearchFilters<Person> filters = SearchFilters.of("unknownField", SearchFilterOperator.IS_EQUAL,
				person.getEmail());
		assertThrows(InvalidDataAccessApiUsageException.class,
				() -> personService.readMultiple(filters, null));
	}

	@Test
	void testReadOne_OK() {
		final String name = "Myname";
		List<Person> persons = IntStream.range(0, 10).mapToObj(i -> {
			Person person = RandomUtils.randomPerson();
			person.setName(name);
			return person;
		}).collect(Collectors.toList());
		personRepository.saveAll(persons);
		SearchFilters<Person> filters = SearchFilters.of("name", SearchFilterOperator.IS_EQUAL, name);
		assertNotNull(personService.readOne(SearchFilters.fromString("-id,!0")));
		assertEquals(name, personService.readOne(filters).getName());
	}

	@Test
	void testReadOne_KO() {
		Person person = personRepository.save(RandomUtils.randomPerson());
		SearchFilters<Person> filters = SearchFilters.of("name.id", SearchFilterOperator.IS_EQUAL, person.getEmail());
		assertThrows(BasicPathUsageException.class, () -> personService.readOne(filters));
	}

	@Test
	void testCreate_OK() {
		assertNotNull(personService.create(RandomUtils.randomPerson()).getId());
	}

	@Test
	void testCreate_KO() {
		assertThrows(NullPointerException.class, () -> personService.create(new Person()));
	}

	@Test
	void testCreateMultiple_OK() {
		List<Person> persons = IntStream.range(0, 10).mapToObj(i -> {
			Person person = RandomUtils.randomPerson();
			person.setName(String.format("name-%d", i));
			return person;
		}).collect(Collectors.toList());
		persons = (List<Person>) personService.createMultiple(persons);
		assertEquals(10, persons.size());
	}

	@Test
	void testCreateMultiple_KO() {
		List<Person> persons = Arrays.asList(new Person[] { new Person(), new Person() });
		assertThrows(NullPointerException.class, () -> personService.createMultiple(persons));
	}

	@Test
	void testUpdate_OK() {
		Person person = personService.create(RandomUtils.randomPerson());
		assertNotNull(person.getId());
		final String newName = "My New Name";
		person.setName(newName);
		personService.update(person.getId(), person);
		assertEquals(newName, personRepository.findById(person.getId()).get().getName());
	}

	@Test
	void testUpdate_KO() {
		Person person = personService.create(RandomUtils.randomPerson());
		person.setEmail("Not valid email");
		assertThrows(TransactionSystemException.class,
				() -> personService.update(person.getId(), person));
	}

	@Test
	void testPatch_OK() {
		Person notValidPerson = personService.create(RandomUtils.randomPerson());
		notValidPerson.setLanguage(null);
		final String newName = "New Name";
		notValidPerson.setName(newName);
		personService.patch(notValidPerson.getId(), notValidPerson);
		Person person = personRepository.findById(notValidPerson.getId()).get();
		assertNotNull(person.getLanguage());
		assertEquals(newName, person.getName());
	}

	@Test
	void testPatch_KO() {
		assertThrows(RequestException.class,
				() -> personService.patch(0L, RandomUtils.randomPerson()));
	}

	@Test
	void testDelete_OK() {
		Person person = personService.create(RandomUtils.randomPerson());
		personService.delete(person.getId());
		assertFalse(personRepository.findById(person.getId()).isPresent());
	}

	@Test
	void testDelete_KO() {
		assertThrows(RequestException.class, () -> personService.delete(0L));
	}

	@Test
	void testDeleteMutiple_OK() {
		Person person1 = personService.create(RandomUtils.randomPerson());
		Person person2 = personService.create(RandomUtils.randomPerson());
		personService.deleteMultiple(Arrays.asList(person1.getId(), person2.getId()));
		assertFalse(personRepository.findById(person1.getId()).isPresent());
		assertFalse(personRepository.findById(person1.getId()).isPresent());
		Person person = personService.create(RandomUtils.randomPerson());
		personService.deleteMultiple(Arrays.asList(person.getId(), 0L));
	}
}
