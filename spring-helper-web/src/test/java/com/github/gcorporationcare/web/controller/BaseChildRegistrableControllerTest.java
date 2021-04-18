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
import com.github.gcorporationcare.notest.common.RandomUtils;
import com.github.gcorporationcare.notest.config.H2Config;
import com.github.gcorporationcare.notest.controller.BaseControllerTest;
import com.github.gcorporationcare.notest.entity.Address;
import com.github.gcorporationcare.notest.entity.Person;
import com.github.gcorporationcare.notest.repository.AddressRepository;
import com.github.gcorporationcare.notest.repository.PersonRepository;

@ActiveProfiles("test")
@SpringBootTest(classes = { ApiStarter.class, H2Config.class })
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class BaseChildRegistrableControllerTest extends BaseControllerTest {

	private static final String PARENT_URL = "persons";
	private static final String ROOT_URL = "addresses";

	Person person;
	@Autowired
	PersonRepository personRepository;
	@Autowired
	AddressRepository addressRepository;

	@BeforeEach
	public void setUp() {
		super.setUp();
		person = personRepository.save(RandomUtils.randomPerson());
		read = String.format("/%s/%d/%s/", PARENT_URL, person.getId(), ROOT_URL);
		readByFilters = String.format("/%s/%d/%s?filters=", PARENT_URL, person.getId(), ROOT_URL);
		readOne = String.format("/%s/%d/%s/first?filters=", PARENT_URL, person.getId(), ROOT_URL);
		create = String.format("/%s/%d/%s", PARENT_URL, person.getId(), ROOT_URL);
		createMultiple = String.format("/%s/%d/%s/multiple", PARENT_URL, person.getId(), ROOT_URL);
		update = String.format("/%s/%d/%s/", PARENT_URL, person.getId(), ROOT_URL);
		patch = String.format("/%s/%d/%s/", PARENT_URL, person.getId(), ROOT_URL);
		delete = String.format("/%s/%d/%s/", PARENT_URL, person.getId(), ROOT_URL);
		deleteMultiple = String.format("/%s/%d/%s/multiple?ids=", PARENT_URL, person.getId(), ROOT_URL);
	}

	@Test
	void testRead_OK() throws Exception {
		Address address = addressRepository.save(RandomUtils.randomAddress(person));
		service.perform(get(String.format("%s%s", read, address.getId())).header(BaseControllerTest.CONTENT_TYPE,
				MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk());

	}

	@Test
	void testRead_KO() throws Exception {
		service.perform(get(String.format("%s%s", read, 0)).header(BaseControllerTest.CONTENT_TYPE,
				MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isNotFound());
	}

	@Test
	void testReadMultiple_OK() throws Exception {
		Address address = addressRepository.save(RandomUtils.randomAddress(person));
		SearchFilters<Address> filters = SearchFilters.of("name", SearchFilterOperator.IS_EQUAL, address.getName());
		service.perform(get(String.format("%s%s", readByFilters, filters.toString()))
				.header(BaseControllerTest.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
	}

	@Test
	void testReadMultiple_KO() throws Exception {
		SearchFilters<Address> filters = SearchFilters.of("created", SearchFilterOperator.IS_EQUAL, "mama");
		service.perform(get(String.format("%s%s", readByFilters, filters)).header(BaseControllerTest.CONTENT_TYPE,
				MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isBadRequest());
	}

	@Test
	void testReadOne_OK() throws Exception {
		List<Address> addresses = IntStream.range(0, 10).mapToObj(i -> {
			Address address = RandomUtils.randomAddress(person);
			address.setCity(String.format("city-%d", i));
			return address;
		}).collect(Collectors.toList());
		addresses = (List<Address>) addressRepository.saveAll(addresses);
		SearchFilters<Address> filters = SearchFilters.of("city", SearchFilterOperator.IS_LIKE, "City");
		service.perform(get(String.format("%s%s", readOne, filters.toString())).header(BaseControllerTest.CONTENT_TYPE,
				MediaType.APPLICATION_JSON_VALUE));
	}

	@Test
	void testReadOne_KO() throws Exception {
		SearchFilters<Address> filters = SearchFilters.of("updated", SearchFilterOperator.IS_LIKE, "name");
		service.perform(get(String.format("%s%s", readOne, filters.toString())).header(BaseControllerTest.CONTENT_TYPE,
				MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isBadRequest());
	}

	@Test
	void testCreate_OK() throws Exception {
		service.perform(post(create).header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.content(toJson(RandomUtils.randomAddress(person), false))).andExpect(status().isCreated());
	}

	@Test
	void testCreate_KO() throws Exception {
		Address address = RandomUtils.randomAddress(person);
		address.setCity(null);
		service.perform(
				post(create).header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).content(toJson(address, false)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void testCreateMultiple_OK() throws Exception {
		List<Address> addresses = Arrays.asList(RandomUtils.randomAddress(person), RandomUtils.randomAddress(person));
		service.perform(post(createMultiple).header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.content(toJson(addresses, false))).andExpect(status().isCreated());
	}

	@Test
	void testCreateMultiple_KO() throws Exception {
		Address address = RandomUtils.randomAddress(person);
		address.setCity(null);
		List<Address> addresses = Arrays.asList(RandomUtils.randomAddress(person), address);
		service.perform(post(createMultiple).header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.content(toJson(addresses, false))).andExpect(status().isBadRequest());
	}

	@Test
	void testUpdate_OK() throws Exception {
		Address address = addressRepository.save(RandomUtils.randomAddress(person));
		address.setCity("My new name");
		service.perform(put(String.format("%s%s", update, address.getId()))
				.header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).content(toJson(address, false)))
				.andExpect(status().isOk());
	}

	@Test
	void testUpdate_KO() throws Exception {
		Address address = addressRepository.save(RandomUtils.randomAddress(person));
		address.setCity(null);
		service.perform(put(String.format("%s%s", update, address.getId()))
				.header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).content(toJson(address, false)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void testPatch_OK() throws Exception {
		Address address = addressRepository.save(RandomUtils.randomAddress(person));
		Address newAddress = new Address();
		newAddress.setCity("alphabet");
		service.perform(patch(String.format("%s%s", patch, address.getId()))
				.header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).content(toJson(newAddress, false)))
				.andExpect(status().isOk());
	}

	@Test
	void testPatch_KO() throws Exception {
		service.perform(patch(String.format("%s%s", patch, 0)).header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.content(toJson(new Address(), false))).andExpect(status().isNotFound());
	}

	@Test
	void testDelete_OK() throws Exception {
		Address address = addressRepository.save(RandomUtils.randomAddress(person));
		address.setActive(false);
		address = addressRepository.save(address);
		service.perform(delete(String.format("%s%s", delete, address.getId())).header(CONTENT_TYPE,
				MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isNoContent());

	}

	@Test
	void testDelete_KO() throws Exception {
		service.perform(delete(String.format("%s%s", delete, 0)).header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isNotFound());
	}

	@Test
	void testDeleteMultiple_OK() throws Exception {
		Address address1 = addressRepository.save(RandomUtils.randomAddress(person));
		address1.setActive(false);
		address1 = addressRepository.save(address1);
		Address address2 = addressRepository.save(RandomUtils.randomAddress(person));
		address2.setActive(false);
		address2 = addressRepository.save(address2);
		String[] ids = new String[] { address1.getId().toString(), address2.getId().toString() };
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
