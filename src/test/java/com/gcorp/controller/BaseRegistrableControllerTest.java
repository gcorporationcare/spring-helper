package com.gcorp.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import com.gcorp.domain.SearchFilter.SearchFilterOperator;
import com.gcorp.ApiStarter;
import com.gcorp.domain.SearchFilters;
import com.gcorp.notest.common.RandomUtils;
import com.gcorp.notest.config.H2Config;
import com.gcorp.notest.controller.BaseControllerTest;
import com.gcorp.notest.entity.Person;
import com.gcorp.notest.repository.PersonRepository;

@ActiveProfiles("test")
@SpringBootTest(classes = { ApiStarter.class, H2Config.class })
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class BaseRegistrableControllerTest extends BaseControllerTest {

	private static final String ROOT_URL = "persons";
	@Autowired
	PersonRepository personRepository;

	@BeforeEach
	public void setUp() {
		super.setUp();
		read = String.format("/%s/", ROOT_URL);
		readByFilters = String.format("/%s?filters=", ROOT_URL);
		readOne = String.format("/%s/first?filters=", ROOT_URL);
		create = String.format("/%s", ROOT_URL);
		createMultiple = String.format("/%s/multiple", ROOT_URL);
		update = String.format("/%s/", ROOT_URL);
		patch = String.format("/%s/", ROOT_URL);
		delete = String.format("/%s/", ROOT_URL);
		deleteMultiple = String.format("/%s/multiple?ids=", ROOT_URL);
	}

	@Test
	void testRead_OK() throws Exception {
		Person person = personRepository.save(RandomUtils.randomPerson());
		service.perform(get(String.format("%s%s", read, person.getId())).header(BaseControllerTest.CONTENT_TYPE,
				MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk());

	}

	@Test
	void testRead_KO() throws Exception {
		service.perform(get(String.format("%s%s", read, 0)).header(BaseControllerTest.CONTENT_TYPE,
				MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isNotFound());
	}

	@Test
	void testReadMultiple_OK() throws Exception {
		Person person = personRepository.save(RandomUtils.randomPerson());
		SearchFilters<Person> filters = SearchFilters.of("name", SearchFilterOperator.IS_EQUAL, person.getName());
		service.perform(get(String.format("%s%s", readByFilters, filters.toString()))
				.header(BaseControllerTest.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
	}

	@Test
	void testReadMultiple_KO() throws Exception {
		SearchFilters<Person> filters = SearchFilters.of("created", SearchFilterOperator.IS_EQUAL, "mama");
		service.perform(get(String.format("%s%s", readByFilters, filters)).header(BaseControllerTest.CONTENT_TYPE,
				MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isBadRequest());
	}

	@Test
	void testReadOne_OK() throws Exception {
		List<Person> persons = IntStream.range(0, 10).mapToObj(i -> {
			Person person = RandomUtils.randomPerson();
			person.setName(String.format("name-%d", i));
			return person;
		}).collect(Collectors.toList());
		persons = (List<Person>) personRepository.saveAll(persons);
		SearchFilters<Person> filters = SearchFilters.of("name", SearchFilterOperator.IS_LIKE, "Name");
		service.perform(get(String.format("%s%s", readOne, filters.toString())).header(BaseControllerTest.CONTENT_TYPE,
				MediaType.APPLICATION_JSON_VALUE));
	}

	@Test
	void testReadOne_KO() throws Exception {
		SearchFilters<Person> filters = SearchFilters.of("updated", SearchFilterOperator.IS_LIKE, "name");
		service.perform(get(String.format("%s%s", readOne, filters.toString())).header(BaseControllerTest.CONTENT_TYPE,
				MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isBadRequest());
	}

	@Test
	void testCreate_OK() throws Exception {
		service.perform(post(create).header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.content(toJson(RandomUtils.randomPerson(), false))).andExpect(status().isCreated());
	}

	@Test
	void testCreate_KO() throws Exception {
		Person existing = personRepository.save(RandomUtils.randomPerson());
		Person person = RandomUtils.randomPerson();
		person.setEmail(existing.getEmail());
		person.setName(null);
		service.perform(
				post(create).header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).content(toJson(person, false)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void testCreateMultiple_OK() throws Exception {
		List<Person> persons = Arrays.asList(RandomUtils.randomPerson(), RandomUtils.randomPerson());
		service.perform(post(createMultiple).header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.content(toJson(persons, false))).andExpect(status().isCreated());
	}

	@Test
	void testCreateMultiple_KO() throws Exception {
		Person person = RandomUtils.randomPerson();
		person.setName(null);
		List<Person> persons = Arrays.asList(RandomUtils.randomPerson(), person);
		service.perform(post(createMultiple).header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.content(toJson(persons, false))).andExpect(status().isBadRequest());
	}

	@Test
	void testUpdate_OK() throws Exception {
		Person person = personRepository.save(RandomUtils.randomPerson());
		person.setName("My new name");
		service.perform(put(String.format("%s%s", update, person.getId()))
				.header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).content(toJson(person, false)))
				.andExpect(status().isOk());
	}

	@Test
	void testUpdate_KO() throws Exception {
		Person person = personRepository.save(RandomUtils.randomPerson());
		person.setName(null);
		service.perform(put(String.format("%s%s", update, person.getId()))
				.header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).content(toJson(person, false)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void testPatch_OK() throws Exception {
		Person person = personRepository.save(RandomUtils.randomPerson());
		Person newPerson = new Person();
		newPerson.setName("alphabet");
		service.perform(patch(String.format("%s%s", patch, person.getId()))
				.header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).content(toJson(newPerson, false)))
				.andExpect(status().isOk());
	}

	@Test
	void testPatch_KO() throws Exception {
		service.perform(patch(String.format("%s%s", patch, 0)).header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.content(toJson(new Person(), false))).andExpect(status().isNotFound());
	}

	@Test
	void testDelete_OK() throws Exception {
		Person person = personRepository.save(RandomUtils.randomPerson());
		service.perform(delete(String.format("%s%s", delete, person.getId())).header(CONTENT_TYPE,
				MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isNoContent());

	}

	@Test
	void testDelete_KO() throws Exception {
		service.perform(delete(String.format("%s%s", delete, 0)).header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isNotFound());
	}

	@Test
	void testDeleteMultiple_OK() throws Exception {
		Person person1 = personRepository.save(RandomUtils.randomPerson());
		Person person2 = personRepository.save(RandomUtils.randomPerson());
		String[] ids = new String[] { person1.getId().toString(), person2.getId().toString() };
		service.perform(delete(String.format("%s%s", deleteMultiple, String.join(MULTIPLE_DELIMITER, ids)))
				.header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
	}

	@Test
	void testDeleteMultiple_KO() throws Exception {
		String[] ids = new String[] { "0", "badValue" };
		service.perform(delete(String.format("%s%s", deleteMultiple, String.join(MULTIPLE_DELIMITER, ids)))
				.header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isBadRequest());
	}
}
