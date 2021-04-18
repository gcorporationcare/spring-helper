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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.TransactionSystemException;

import com.github.gcorporationcare.ApiStarter;
import com.github.gcorporationcare.data.domain.FieldFilter;
import com.github.gcorporationcare.data.domain.SearchFilter.SearchFilterOperator;
import com.github.gcorporationcare.data.domain.SearchFilters;
import com.github.gcorporationcare.notest.common.RandomUtils;
import com.github.gcorporationcare.notest.config.H2Config;
import com.github.gcorporationcare.notest.entity.Address;
import com.github.gcorporationcare.notest.entity.Person;
import com.github.gcorporationcare.notest.repository.AddressRepository;
import com.github.gcorporationcare.notest.repository.PersonRepository;
import com.github.gcorporationcare.notest.service.AddressService;
import com.github.gcorporationcare.web.exception.RequestException;

@ActiveProfiles("test")
@SpringBootTest(classes = { ApiStarter.class, H2Config.class })
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class BaseChildRegistrableServiceTest {

	Person person;
	@Autowired
	AddressService addressService;
	@Autowired
	PersonRepository personRepository;
	@Autowired
	AddressRepository addressRepository;
	FieldFilter<Address> allFieldFilter = FieldFilter.allFields();
	FieldFilter<Address> defaultFieldFilter = FieldFilter.defaultFields();

	@BeforeEach
	public void setUp() {
		person = personRepository.save(RandomUtils.randomPerson());
	}

	@Test
	void testRead_OK() {
		Address address = addressRepository.save(RandomUtils.randomAddress(person));
		Address addressExist = addressService.read(person.getId(), address.getId(), allFieldFilter);
		assertEquals(address.getId(), addressExist.getId());
		assertEquals(address.getName(), addressExist.getName());
		assertEquals(address.isActive(), addressExist.isActive());
	}

	@Test
	void testRead_KO() {
		assertThrows(RequestException.class, () -> addressService.read(person.getId(), 0L, allFieldFilter));
	}

	@Test
	void testReadMultiple_OK() {
		final String street = "mystreet";
		List<Address> addresses = IntStream.range(0, 10).mapToObj(i -> {
			Address address = RandomUtils.randomAddress(person);
			if (i % 3 == 0) {
				address.setStreet(street);
			}
			return address;
		}).collect(Collectors.toList());
		addressRepository.saveAll(addresses);
		SearchFilters<Address> filters = SearchFilters.of("street", SearchFilterOperator.IS_EQUAL, street);
		Page<Address> addressesPage = addressService.readMultiple(person.getId(), filters, allFieldFilter, null);
		assertEquals(4, addressesPage.getTotalElements());
	}

	@Test
	void testReadMultiple_KO() {
		Address address = addressRepository.save(RandomUtils.randomAddress(person));
		SearchFilters<Address> filters = SearchFilters.of("owner.id", SearchFilterOperator.IS_EQUAL, person.getId());
		assertThrows(InvalidDataAccessApiUsageException.class,
				() -> addressService.readMultiple(address.getPerson().getId(), filters, allFieldFilter, null));
	}

	@Test
	void testReadOne_OK() {
		final String city = "Mycity";
		List<Address> addresses = IntStream.range(0, 10).mapToObj(i -> {
			Address address = RandomUtils.randomAddress(person);
			address.setCity(city);
			return address;
		}).collect(Collectors.toList());
		addressRepository.saveAll(addresses);
		SearchFilters<Address> filters = SearchFilters.of("city", SearchFilterOperator.IS_EQUAL, city);
		assertNotNull(addressService.readOne(person.getId(), SearchFilters.fromString("-id,!0"), allFieldFilter));
		assertEquals(city, addressService.readOne(person.getId(), filters, allFieldFilter).getCity());
	}

	@Test
	void testReadOne_KO() {
		Address address = addressRepository.save(RandomUtils.randomAddress(person));
		SearchFilters<Address> filters = SearchFilters.of("name.id", SearchFilterOperator.IS_EQUAL, address.getName());
		assertThrows(BasicPathUsageException.class,
				() -> addressService.readOne(person.getId(), filters, allFieldFilter));
	}

	@Test
	void testCreate_OK() {
		Address address = addressService.create(person.getId(), RandomUtils.randomAddress(person), allFieldFilter);
		assertNotNull(address.getId());
		assertEquals(person.getId(), address.getPerson().getId());
	}

	@Test
	void testCreate_KO() {
		assertThrows(NullPointerException.class,
				() -> addressService.create(person.getId(), new Address(), defaultFieldFilter));
	}

	@Test
	void testCreateMultiple_OK() {
		List<Address> addresses = IntStream.range(0, 10).mapToObj(i -> {
			Address address = RandomUtils.randomAddress(person);
			return address;
		}).collect(Collectors.toList());
		addresses = (List<Address>) addressService.createMultiple(person.getId(), addresses, allFieldFilter);
		assertEquals(10, addresses.size());
		Address address = addresses.get(RandomUtils.randomInteger(9));
		assertEquals(person.getId(), address.getPerson().getId());
	}

	@Test
	void testCreateMultiple_KO() {
		List<Address> addresses = Arrays.asList(new Address[] { RandomUtils.randomAddress(person), new Address() });
		assertThrows(NullPointerException.class,
				() -> addressService.createMultiple(person.getId(), addresses, defaultFieldFilter));
	}

	@Test
	void testUpdate_OK() {
		Address address = addressService.create(person.getId(), RandomUtils.randomAddress(person), allFieldFilter);
		assertNotNull(address.getId());
		final String newStreet = "My New street";
		address.setStreet(newStreet);
		addressService.update(person.getId(), address.getId(), address, allFieldFilter);
		assertEquals(newStreet, addressRepository.findById(address.getId()).get().getStreet());
	}

	@Test
	void testUpdate_KO() {
		Address address = addressService.create(person.getId(), RandomUtils.randomAddress(person), defaultFieldFilter);
		address.setCity("");
		assertThrows(TransactionSystemException.class,
				() -> addressService.update(person.getId(), address.getId(), address, defaultFieldFilter));
	}

	@Test
	void testPatch_OK() {
		Address notValidAddress = addressService.create(person.getId(), RandomUtils.randomAddress(person),
				defaultFieldFilter);
		notValidAddress.setCity(null);
		final String newState = "New State";
		notValidAddress.setState(newState);
		addressService.patch(person.getId(), notValidAddress.getId(), notValidAddress, defaultFieldFilter);
		Address address = addressRepository.findById(notValidAddress.getId()).get();
		assertNotNull(address.getCity());
		assertEquals(newState, address.getState());
	}

	@Test
	void testPatch_KO() {
		assertThrows(RequestException.class,
				() -> addressService.patch(person.getId(), 0L, RandomUtils.randomAddress(person), defaultFieldFilter));
	}

	@Test
	void testDelete_OK() {
		Address address = addressService.create(person.getId(), RandomUtils.randomAddress(person), allFieldFilter);
		address.setActive(false);
		addressRepository.save(address);
		addressService.delete(person.getId(), address.getId());
		assertFalse(addressRepository.findById(address.getId()).isPresent());
	}

	@Test
	void testDelete_KO() {
		Address address = addressService.create(person.getId(), RandomUtils.randomAddress(person), defaultFieldFilter);
		assertThrows(RequestException.class, () -> addressService.delete(person.getId(), address.getId()));

	}

	@Test
	void testDeleteMutiple_OK() {
		Address address1 = addressService.create(person.getId(), RandomUtils.randomAddress(person), allFieldFilter);
		Address address2 = addressService.create(person.getId(), RandomUtils.randomAddress(person), allFieldFilter);
		address1.setActive(false);
		address2.setActive(false);
		address1 = addressRepository.save(address1);
		address2 = addressRepository.save(address2);
		addressService.deleteMultiple(person.getId(), Arrays.asList(address1.getId(), address2.getId()));
		assertFalse(addressRepository.findById(address1.getId()).isPresent());
		assertFalse(addressRepository.findById(address2.getId()).isPresent());
		Address address = addressService.create(person.getId(), RandomUtils.randomAddress(person), allFieldFilter);
		address.setActive(false);
		address = addressRepository.save(address);
		addressService.deleteMultiple(person.getId(), Arrays.asList(address.getId(), 0L));
	}
}
