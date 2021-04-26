package com.github.gcorporationcare.web.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
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
import com.github.gcorporationcare.data.domain.SearchFilter.SearchFilterOperator;
import com.github.gcorporationcare.data.domain.SearchFilters;
import com.github.gcorporationcare.notest.common.RandomUtils;
import com.github.gcorporationcare.notest.config.H2Config;
import com.github.gcorporationcare.notest.entity.Address;
import com.github.gcorporationcare.notest.entity.IdentityCard;
import com.github.gcorporationcare.notest.entity.Person;
import com.github.gcorporationcare.notest.repository.AddressRepository;
import com.github.gcorporationcare.notest.repository.IdentityCardRepository;
import com.github.gcorporationcare.notest.repository.PersonRepository;
import com.github.gcorporationcare.notest.service.AddressService;
import com.github.gcorporationcare.notest.service.IdentityCardService;
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
	@Autowired
	IdentityCardService identityCardService;
	@Autowired
	IdentityCardRepository identityCardRepository;

	@BeforeEach
	public void setUp() {
		person = personRepository.save(RandomUtils.randomPerson());
	}

	@Test
	void testRead_OK() {
		Address address = addressRepository.save(RandomUtils.randomAddress(person));
		Address addressExist = addressService.read(person.getId(), address.getId());
		assertEquals(address.getId(), addressExist.getId());
		assertEquals(address.getName(), addressExist.getName());
		assertEquals(address.isActive(), addressExist.isActive());
	}

	@Test
	void testRead_KO() {
		assertThrows(RequestException.class, () -> addressService.read(person.getId(), 0L));
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
		Page<Address> addressesPage = addressService.readMultiple(person.getId(), filters, null);
		assertEquals(4, addressesPage.getTotalElements());
	}

	@Test
	void testReadMultiple_KO() {
		Address address = addressRepository.save(RandomUtils.randomAddress(person));
		SearchFilters<Address> filters = SearchFilters.of("owner.id", SearchFilterOperator.IS_EQUAL, person.getId());
		assertThrows(InvalidDataAccessApiUsageException.class,
				() -> addressService.readMultiple(address.getPerson().getId(), filters, null));
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
		assertNotNull(addressService.readOne(person.getId(), SearchFilters.fromString("-id,!0")));
		assertEquals(city, addressService.readOne(person.getId(), filters).getCity());
	}

	@Test
	void testReadOne_KO() {
		Address address = addressRepository.save(RandomUtils.randomAddress(person));
		SearchFilters<Address> filters = SearchFilters.of("name.id", SearchFilterOperator.IS_EQUAL, address.getName());
		assertThrows(BasicPathUsageException.class, () -> addressService.readOne(person.getId(), filters));
	}

	@Test
	void testCreate_OK() {
		Address address = addressService.create(person.getId(), RandomUtils.randomAddress(person));
		assertNotNull(address.getId());
		assertEquals(person.getId(), address.getPerson().getId());
	}

	@Test
	void testCreate_KO() {
		assertThrows(NullPointerException.class, () -> addressService.create(person.getId(), new Address()));
	}

	@Test
	void testCreateMultiple_OK() {
		List<Address> addresses = IntStream.range(0, 10).mapToObj(i -> {
			Address address = RandomUtils.randomAddress(person);
			return address;
		}).collect(Collectors.toList());
		addresses = (List<Address>) addressService.createMultiple(person.getId(), addresses);
		assertEquals(10, addresses.size());
		Address address = addresses.get(RandomUtils.randomInteger(9));
		assertEquals(person.getId(), address.getPerson().getId());
	}

	@Test
	void testCreateMultiple_KO() {
		List<Address> addresses = Arrays.asList(new Address[] { RandomUtils.randomAddress(person), new Address() });
		assertThrows(NullPointerException.class, () -> addressService.createMultiple(person.getId(), addresses));
	}

	@Test
	void testUpdate_OK() {
		Address address = addressService.create(person.getId(), RandomUtils.randomAddress(person));
		assertNotNull(address.getId());
		final String newStreet = "My New street";
		address.setStreet(newStreet);
		addressService.update(person.getId(), address.getId(), address);
		assertEquals(newStreet, addressRepository.findById(address.getId()).get().getStreet());
	}

	@Test
	void testUpdate_KO() {
		Address address = addressService.create(person.getId(), RandomUtils.randomAddress(person));
		address.setCity("");
		assertThrows(TransactionSystemException.class,
				() -> addressService.update(person.getId(), address.getId(), address));
	}

	@Test
	void testPatch_OK() {
		Address notValidAddress = addressService.create(person.getId(), RandomUtils.randomAddress(person));
		notValidAddress.setCity(null);
		final String newState = "New State";
		notValidAddress.setState(newState);
		addressService.patch(person.getId(), notValidAddress.getId(), notValidAddress);
		Address address = addressRepository.findById(notValidAddress.getId()).get();
		assertNotNull(address.getCity());
		assertEquals(newState, address.getState());
	}

	@Test
	void testPatch_KO() {
		assertThrows(RequestException.class,
				() -> addressService.patch(person.getId(), 0L, RandomUtils.randomAddress(person)));
	}

	@Test
	void testDelete_OK() {
		Address address = addressService.create(person.getId(), RandomUtils.randomAddress(person));
		address.setActive(false);
		addressRepository.save(address);
		addressService.delete(person.getId(), address.getId());
		assertFalse(addressRepository.findById(address.getId()).isPresent());
	}

	@Test
	void testDelete_KO() {
		Address address = addressService.create(person.getId(), RandomUtils.randomAddress(person));
		assertThrows(RequestException.class, () -> addressService.delete(person.getId(), address.getId()));

	}

	@Test
	void testDeleteMutiple_OK() {
		Address address1 = addressService.create(person.getId(), RandomUtils.randomAddress(person));
		Address address2 = addressService.create(person.getId(), RandomUtils.randomAddress(person));
		address1.setActive(false);
		address2.setActive(false);
		address1 = addressRepository.save(address1);
		address2 = addressRepository.save(address2);
		addressService.deleteMultiple(person.getId(), Arrays.asList(address1.getId(), address2.getId()));
		assertFalse(addressRepository.findById(address1.getId()).isPresent());
		assertFalse(addressRepository.findById(address2.getId()).isPresent());
		Address address = addressService.create(person.getId(), RandomUtils.randomAddress(person));
		address.setActive(false);
		address = addressRepository.save(address);
		addressService.deleteMultiple(person.getId(), Arrays.asList(address.getId(), 0L));
	}

	@Test
	void testReadWithNull() {
		IdentityCard identityCard1 = identityCardRepository
				.save(new IdentityCard(UUID.randomUUID().toString(), person));
		IdentityCard identityCard2 = identityCardRepository
				.save(new IdentityCard(UUID.randomUUID().toString(), person));

		List<IdentityCard> identityCards = identityCardRepository.findByPersonId(person.getId());
		assertEquals(identityCards.size(), 2);
		identityCardService.delete(person.getId(), identityCard1.getId());
		List<IdentityCard> currentIdentityCards = identityCardRepository.findByPersonId(person.getId());
		assertEquals(currentIdentityCards.size(), 1);

		assertTrue(identityCardRepository.findById(identityCard1.getId()).isPresent());
		assertTrue(identityCardRepository.findById(identityCard2.getId()).isPresent());
	}

	@Test
	void testSoftdelete() {
		assertThrows(RequestException.class, () -> identityCardService.read(person.getId(), null));
	}
}
