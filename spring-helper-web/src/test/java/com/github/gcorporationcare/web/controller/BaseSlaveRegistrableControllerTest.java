package com.github.gcorporationcare.web.controller;

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

import com.github.gcorporationcare.ApiStarter;
import com.github.gcorporationcare.data.domain.SearchFilter.SearchFilterOperator;
import com.github.gcorporationcare.data.domain.SearchFilters;
import com.github.gcorporationcare.data.field.Country;
import com.github.gcorporationcare.notest.common.RandomUtils;
import com.github.gcorporationcare.notest.config.H2Config;
import com.github.gcorporationcare.notest.controller.BaseControllerTest;
import com.github.gcorporationcare.notest.entity.Address;
import com.github.gcorporationcare.notest.entity.Person;
import com.github.gcorporationcare.notest.entity.PersonTag;
import com.github.gcorporationcare.notest.repository.PersonRepository;
import com.github.gcorporationcare.notest.repository.PersonTagRepository;

@ActiveProfiles("test")
@SpringBootTest(classes = { ApiStarter.class, H2Config.class })
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class BaseSlaveRegistrableControllerTest extends BaseControllerTest {

	private static final String MASTER_URL = "persons";
	private static final String ROOT_URL = "tags";

	Person person;
	Country country;
	@Autowired
	PersonRepository personRepository;
	@Autowired
	PersonTagRepository personTagRepository;

	@BeforeEach
	public void setUp() {
		super.setUp();
		country = Country.find("us");
		person = personRepository.save(RandomUtils.randomPerson());
		read = String.format("/%s/%d/%s/", MASTER_URL, person.getId(), ROOT_URL);
		readByFilters = String.format("/%s/%d/%s?filters=", MASTER_URL, person.getId(), ROOT_URL);
		readOne = String.format("/%s/%d/%s/first?filters=", MASTER_URL, person.getId(), ROOT_URL);
		create = String.format("/%s/%d/%s", MASTER_URL, person.getId(), ROOT_URL);
		createMultiple = String.format("/%s/%d/%s/multiple", MASTER_URL, person.getId(), ROOT_URL);
		update = String.format("/%s/%d/%s/", MASTER_URL, person.getId(), ROOT_URL);
		patch = String.format("/%s/%d/%s/", MASTER_URL, person.getId(), ROOT_URL);
		delete = String.format("/%s/%d/%s/", MASTER_URL, person.getId(), ROOT_URL);
		deleteMultiple = String.format("/%s/%d/%s/multiple?ids=", MASTER_URL, person.getId(), ROOT_URL);
	}

	@Test
	void testRead_OK() throws Exception {
		PersonTag personTag = personTagRepository.save(RandomUtils.randomPersonTag(person, null));
		service.perform(get(String.format("%s%s", read, personTag.getId())).header(BaseControllerTest.CONTENT_TYPE,
				MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk());

	}

	@Test
	void testRead_KO() throws Exception {
		service.perform(get(String.format("%s%s", read, 0)).header(BaseControllerTest.CONTENT_TYPE,
				MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isNotFound());
	}

	@Test
	void testReadMultiple_OK() throws Exception {
		PersonTag personTag = personTagRepository.save(RandomUtils.randomPersonTag(person, Country.find("us")));
		SearchFilters<PersonTag> filters = SearchFilters.of("name", SearchFilterOperator.IS_EQUAL, personTag.getName());
		service.perform(get(String.format("%s%s", readByFilters, filters.toString()))
				.header(BaseControllerTest.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk());
	}

	@Test
	void testReadMultiple_KO() throws Exception {
		SearchFilters<PersonTag> filters = SearchFilters.of("created", SearchFilterOperator.IS_EQUAL, "mama");
		service.perform(get(String.format("%s%s", readByFilters, filters)).header(BaseControllerTest.CONTENT_TYPE,
				MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isBadRequest());
	}

	@Test
	void testReadOne_OK() throws Exception {
		List<PersonTag> personTags = IntStream.range(0, 10).mapToObj(i -> {
			PersonTag personTag = RandomUtils.randomPersonTag(person, country);
			personTag.setName(String.format("tag-%d", i));
			return personTag;
		}).collect(Collectors.toList());
		personTags = (List<PersonTag>) personTagRepository.saveAll(personTags);
		SearchFilters<Address> filters = SearchFilters.of("name", SearchFilterOperator.IS_LIKE, "Tag");
		service.perform(get(String.format("%s%s", readOne, filters.toString())).header(BaseControllerTest.CONTENT_TYPE,
				MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk());
	}

	@Test
	void testReadOne_KO() throws Exception {
		// With empty filters
		service.perform(get(String.format("%s?filters=", readOne)).header(BaseControllerTest.CONTENT_TYPE,
				MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isBadRequest());
	}

	@Test
	void testCreate_OK() throws Exception {
		service.perform(post(create).header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.content(toJson(RandomUtils.randomPersonTag(person, country), false))).andExpect(status().isCreated());
	}

	@Test
	void testCreate_KO() throws Exception {
		PersonTag personTag = personTagRepository.save(RandomUtils.randomPersonTag(person, null));
		personTag.getDescription().setDescription(null);
		service.perform(
				post(create).header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).content(toJson(personTag, false)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void testCreateMultiple_OK() throws Exception {
		List<PersonTag> personTags = Arrays.asList(RandomUtils.randomPersonTag(person, country),
				RandomUtils.randomPersonTag(person, country));
		service.perform(post(createMultiple).header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.content(toJson(personTags, false))).andExpect(status().isCreated());
	}

	@Test
	void testCreateMultiple_KO() throws Exception {
		PersonTag personTag = RandomUtils.randomPersonTag(person, country);
		personTag.setName(null);
		List<PersonTag> personTags = Arrays.asList(RandomUtils.randomPersonTag(person, null), personTag);
		service.perform(post(createMultiple).header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.content(toJson(personTags, false))).andExpect(status().isBadRequest());
	}

	@Test
	void testUpdate_OK() throws Exception {
		PersonTag personTag = personTagRepository.save(RandomUtils.randomPersonTag(person, country));
		personTag.setName("My new name");
		service.perform(put(String.format("%s%s", update, personTag.getId()))
				.header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).content(toJson(personTag, false)))
				.andExpect(status().isOk());
	}

	@Test
	void testUpdate_KO() throws Exception {
		PersonTag personTag = personTagRepository.save(RandomUtils.randomPersonTag(person, country));
		personTag.setName(null);
		service.perform(put(String.format("%s%s", update, personTag.getId()))
				.header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).content(toJson(personTag, false)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void testPatch_OK() throws Exception {
		PersonTag personTag = personTagRepository.save(RandomUtils.randomPersonTag(person, null));
		PersonTag newPersonTag = new PersonTag();
		newPersonTag.setName("alphabet");
		newPersonTag.setId(personTag.getId());
		service.perform(patch(String.format("%s%s", patch, personTag.getId()))
				.header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).content(toJson(newPersonTag, false)))
				.andExpect(status().isOk());
	}

	@Test
	void testPatch_KO() throws Exception {
		PersonTag personTag = new PersonTag();
		personTag.setId(0L);
		service.perform(patch(String.format("%s%s", patch, 0)).header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.content(toJson(personTag, false))).andExpect(status().isNotFound());
	}

	@Test
	void testDelete_OK() throws Exception {
		PersonTag personTag = personTagRepository.save(RandomUtils.randomPersonTag(person, country));
		personTag = personTagRepository.save(personTag);
		service.perform(delete(String.format("%s%s", delete, personTag.getId())).header(CONTENT_TYPE,
				MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isNoContent());

	}

	@Test
	void testDelete_KO() throws Exception {
		service.perform(delete(String.format("%s%s", delete, 0)).header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isNotFound());
	}

	@Test
	void testDeleteMultiple_OK() throws Exception {
		PersonTag personTag1 = personTagRepository.save(RandomUtils.randomPersonTag(person, country));
		personTag1 = personTagRepository.save(personTag1);
		PersonTag personTag2 = personTagRepository.save(RandomUtils.randomPersonTag(person, country));
		personTag2 = personTagRepository.save(personTag2);
		String[] ids = new String[] { personTag1.getId().toString(), personTag2.getId().toString() };
		service.perform(delete(String.format("%s%s", deleteMultiple, String.join(MULTIPLE_DELIMITER, ids)))
				.header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isNoContent());
	}

	@Test
	void testDeleteMultiple_KO() throws Exception {
		String[] ids = new String[] { "0", "badValue" };
		service.perform(delete(String.format("%s%s", deleteMultiple, String.join(MULTIPLE_DELIMITER, ids)))
				.header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isBadRequest());
	}
}
