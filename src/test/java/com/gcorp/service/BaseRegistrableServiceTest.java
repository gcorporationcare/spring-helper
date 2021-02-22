package com.gcorp.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.hibernate.query.criteria.internal.BasicPathUsageException;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.TransactionSystemException;

import com.gcorp.ApiStarter;
import com.gcorp.domain.FieldFilter;
import com.gcorp.domain.SearchFilter.SearchFilterOperator;
import com.gcorp.domain.SearchFilters;
import com.gcorp.exception.RequestException;
import com.gcorp.notest.common.RandomUtils;
import com.gcorp.notest.config.H2Config;
import com.gcorp.notest.entity.Person;
import com.gcorp.notest.repository.PersonRepository;
import com.gcorp.notest.service.PersonService;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ApiStarter.class, H2Config.class })
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BaseRegistrableServiceTest {

	@Autowired
	PersonService personService;
	@Autowired
	PersonRepository personRepository;
	
	FieldFilter<Person> allFieldFilter = FieldFilter.allFields();
	FieldFilter<Person> defaultFieldFilter = FieldFilter.defaultFields();

	@Test
	public void testRead_OK() {
		Person person = personRepository.save(RandomUtils.randomPerson());
		Person personExist = personService.read(person.getId(), allFieldFilter);
		Assert.assertEquals(person.getId(), personExist.getId());
		Assert.assertEquals(person.getEmail(), personExist.getEmail());
		Assert.assertEquals(person.getName(), personExist.getName());
		Assert.assertEquals(person.getLanguage(), personExist.getLanguage());
	}

	@Test(expected = RequestException.class)
	public void testRead_KO() {
		personService.read(0L, allFieldFilter);
	}

	@Test
	public void testReadMultiple_OK() {
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
		Page<Person> personsPage = personService.readMultiple(filters, allFieldFilter, null);
		Assert.assertEquals(5, personsPage.getTotalElements());
	}

	@Test(expected = InvalidDataAccessApiUsageException.class)
	public void testReadMultiple_KO() {
		Person person = personRepository.save(RandomUtils.randomPerson());
		SearchFilters<Person> filters = SearchFilters.of("unknownField", SearchFilterOperator.IS_EQUAL,
				person.getEmail());
		personService.readMultiple(filters, allFieldFilter, null);
	}

	@Test
	public void testReadOne_OK() {
		final String name = "Myname";
		List<Person> persons = IntStream.range(0, 10).mapToObj(i -> {
			Person person = RandomUtils.randomPerson();
			person.setName(name);
			return person;
		}).collect(Collectors.toList());
		personRepository.saveAll(persons);
		SearchFilters<Person> filters = SearchFilters.of("name", SearchFilterOperator.IS_EQUAL, name);
		Assert.assertNotNull(personService.readOne(SearchFilters.fromString("-id,!0"), allFieldFilter));
		Assert.assertEquals(name, personService.readOne(filters, allFieldFilter).getName());
	}

	@Test(expected = BasicPathUsageException.class)
	public void testReadOne_KO() {
		Person person = personRepository.save(RandomUtils.randomPerson());
		SearchFilters<Person> filters = SearchFilters.of("name.id", SearchFilterOperator.IS_EQUAL, person.getEmail());
		personService.readOne(filters, allFieldFilter);
	}

	@Test
	public void testCreate_OK() {
		Assert.assertNotNull(personService.create(RandomUtils.randomPerson(), defaultFieldFilter).getId());
	}

	@Test(expected = NullPointerException.class)
	public void testCreate_KO() {
		personService.create(new Person(), defaultFieldFilter);
	}

	@Test
	public void testCreateMultiple_OK() {
		List<Person> persons = IntStream.range(0, 10).mapToObj(i -> {
			Person person = RandomUtils.randomPerson();
			person.setName(String.format("name-%d", i));
			return person;
		}).collect(Collectors.toList());
		persons = (List<Person>) personService.createMultiple(persons, defaultFieldFilter);
		Assert.assertEquals(10, persons.size());
	}

	@Test(expected = NullPointerException.class)
	public void testCreateMultiple_KO() {
		List<Person> persons = Arrays.asList(new Person[] { new Person(), new Person() });
		personService.createMultiple(persons, defaultFieldFilter);
	}

	@Test
	public void testUpdate_OK() {
		Person person = personService.create(RandomUtils.randomPerson(), defaultFieldFilter);
		Assert.assertNotNull(person.getId());
		final String newName = "My New Name";
		person.setName(newName);
		personService.update(person.getId(), person, defaultFieldFilter);
		Assert.assertEquals(newName, personRepository.findById(person.getId()).get().getName());
	}

	@Test(expected = TransactionSystemException.class)
	public void testUpdate_KO() {
		Person person = personService.create(RandomUtils.randomPerson(), defaultFieldFilter);
		person.setEmail("Not valid email");
		personService.update(person.getId(), person, defaultFieldFilter);
	}

	@Test
	public void testPatch_OK() {
		Person notValidPerson = personService.create(RandomUtils.randomPerson(), defaultFieldFilter);
		notValidPerson.setLanguage(null);
		final String newName = "New Name";
		notValidPerson.setName(newName);
		personService.patch(notValidPerson.getId(), notValidPerson, defaultFieldFilter);
		Person person = personRepository.findById(notValidPerson.getId()).get();
		Assert.assertNotNull(person.getLanguage());
		Assert.assertEquals(newName, person.getName());
	}

	@Test(expected = RequestException.class)
	public void testPatch_KO() {
		personService.patch(0L, RandomUtils.randomPerson(), defaultFieldFilter);
	}

	@Test
	public void testDelete_OK() {
		Person person = personService.create(RandomUtils.randomPerson(), defaultFieldFilter);
		personService.delete(person.getId());
		Assert.assertFalse(personRepository.findById(person.getId()).isPresent());
	}

	@Test(expected = RequestException.class)
	public void testDelete_KO() {
		personService.delete(0L);
	}

	@Test
	public void testDeleteMutiple_OK() {
		Person person1 = personService.create(RandomUtils.randomPerson(), defaultFieldFilter);
		Person person2 = personService.create(RandomUtils.randomPerson(), defaultFieldFilter);
		personService.deleteMultiple(Arrays.asList(person1.getId(), person2.getId()));
		Assert.assertFalse(personRepository.findById(person1.getId()).isPresent());
		Assert.assertFalse(personRepository.findById(person1.getId()).isPresent());
		Person person = personService.create(RandomUtils.randomPerson(), defaultFieldFilter);
		personService.deleteMultiple(Arrays.asList(person.getId(), 0L));
	}
}
