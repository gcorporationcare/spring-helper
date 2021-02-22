package com.gcorp.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.hibernate.query.criteria.internal.BasicPathUsageException;
import org.junit.Assert;
import org.junit.Before;
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
import com.gcorp.notest.entity.Address;
import com.gcorp.notest.entity.Person;
import com.gcorp.notest.repository.AddressRepository;
import com.gcorp.notest.repository.PersonRepository;
import com.gcorp.notest.service.AddressService;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ApiStarter.class, H2Config.class })
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BaseChildRegistrableServiceTest {

	Person person;
	@Autowired
	AddressService addressService;
	@Autowired
	PersonRepository personRepository;
	@Autowired
	AddressRepository addressRepository;
	FieldFilter<Address> allFieldFilter = FieldFilter.allFields();
	FieldFilter<Address> defaultFieldFilter = FieldFilter.defaultFields();

	@Before
	public void setUp() {
		person = personRepository.save(RandomUtils.randomPerson());
	}

	@Test
	public void testRead_OK() {
		Address address = addressRepository.save(RandomUtils.randomAddress(person));
		Address addressExist = addressService.read(person.getId(), address.getId(), allFieldFilter);
		Assert.assertEquals(address.getId(), addressExist.getId());
		Assert.assertEquals(address.getName(), addressExist.getName());
		Assert.assertEquals(address.isActive(), addressExist.isActive());
	}

	@Test(expected = RequestException.class)
	public void testRead_KO() {
		addressService.read(person.getId(), 0L, allFieldFilter);
	}

	@Test
	public void testReadMultiple_OK() {
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
		Assert.assertEquals(4, addressesPage.getTotalElements());
	}

	@Test(expected = InvalidDataAccessApiUsageException.class)
	public void testReadMultiple_KO() {
		Address address = addressRepository.save(RandomUtils.randomAddress(person));
		SearchFilters<Address> filters = SearchFilters.of("owner.id", SearchFilterOperator.IS_EQUAL, person.getId());
		addressService.readMultiple(address.getPerson().getId(), filters, allFieldFilter, null);
	}

	@Test
	public void testReadOne_OK() {
		final String city = "Mycity";
		List<Address> addresses = IntStream.range(0, 10).mapToObj(i -> {
			Address address = RandomUtils.randomAddress(person);
			address.setCity(city);
			return address;
		}).collect(Collectors.toList());
		addressRepository.saveAll(addresses);
		SearchFilters<Address> filters = SearchFilters.of("city", SearchFilterOperator.IS_EQUAL, city);
		Assert.assertNotNull(
				addressService.readOne(person.getId(), SearchFilters.fromString("-id,!0"), allFieldFilter));
		Assert.assertEquals(city, addressService.readOne(person.getId(), filters, allFieldFilter).getCity());
	}

	@Test(expected = BasicPathUsageException.class)
	public void testReadOne_KO() {
		Address address = addressRepository.save(RandomUtils.randomAddress(person));
		SearchFilters<Address> filters = SearchFilters.of("name.id", SearchFilterOperator.IS_EQUAL, address.getName());
		addressService.readOne(person.getId(), filters, allFieldFilter);
	}

	@Test
	public void testCreate_OK() {
		Address address = addressService.create(person.getId(), RandomUtils.randomAddress(person), allFieldFilter);
		Assert.assertNotNull(address.getId());
		Assert.assertEquals(person.getId(), address.getPerson().getId());
	}

	@Test(expected = NullPointerException.class)
	public void testCreate_KO() {
		addressService.create(person.getId(), new Address(), defaultFieldFilter);
	}

	@Test
	public void testCreateMultiple_OK() {
		List<Address> addresses = IntStream.range(0, 10).mapToObj(i -> {
			Address address = RandomUtils.randomAddress(person);
			return address;
		}).collect(Collectors.toList());
		addresses = (List<Address>) addressService.createMultiple(person.getId(), addresses, allFieldFilter);
		Assert.assertEquals(10, addresses.size());
		Address address = addresses.get(RandomUtils.randomInteger(9));
		Assert.assertEquals(person.getId(), address.getPerson().getId());
	}

	@Test(expected = NullPointerException.class)
	public void testCreateMultiple_KO() {
		List<Address> addresses = Arrays.asList(new Address[] { RandomUtils.randomAddress(person), new Address() });
		addressService.createMultiple(person.getId(), addresses, defaultFieldFilter);
	}

	@Test
	public void testUpdate_OK() {
		Address address = addressService.create(person.getId(), RandomUtils.randomAddress(person), allFieldFilter);
		Assert.assertNotNull(address.getId());
		final String newStreet = "My New street";
		address.setStreet(newStreet);
		addressService.update(person.getId(), address.getId(), address, allFieldFilter);
		Assert.assertEquals(newStreet, addressRepository.findById(address.getId()).get().getStreet());
	}

	@Test(expected = TransactionSystemException.class)
	public void testUpdate_KO() {
		Address address = addressService.create(person.getId(), RandomUtils.randomAddress(person), defaultFieldFilter);
		address.setCity("");
		addressService.update(person.getId(), address.getId(), address, defaultFieldFilter);
	}

	@Test
	public void testPatch_OK() {
		Address notValidAddress = addressService.create(person.getId(), RandomUtils.randomAddress(person),
				defaultFieldFilter);
		notValidAddress.setCity(null);
		final String newState = "New State";
		notValidAddress.setState(newState);
		addressService.patch(person.getId(), notValidAddress.getId(), notValidAddress, defaultFieldFilter);
		Address address = addressRepository.findById(notValidAddress.getId()).get();
		Assert.assertNotNull(address.getCity());
		Assert.assertEquals(newState, address.getState());
	}

	@Test(expected = RequestException.class)
	public void testPatch_KO() {
		addressService.patch(person.getId(), 0L, RandomUtils.randomAddress(person), defaultFieldFilter);
	}

	@Test
	public void testDelete_OK() {
		Address address = addressService.create(person.getId(), RandomUtils.randomAddress(person), allFieldFilter);
		address.setActive(false);
		addressRepository.save(address);
		addressService.delete(person.getId(), address.getId());
		Assert.assertFalse(addressRepository.findById(address.getId()).isPresent());
	}

	@Test(expected = RequestException.class)
	public void testDelete_KO() {
		Address address = addressService.create(person.getId(), RandomUtils.randomAddress(person), defaultFieldFilter);
		addressService.delete(person.getId(), address.getId());
	}

	@Test
	public void testDeleteMutiple_OK() {
		Address address1 = addressService.create(person.getId(), RandomUtils.randomAddress(person), allFieldFilter);
		Address address2 = addressService.create(person.getId(), RandomUtils.randomAddress(person), allFieldFilter);
		address1.setActive(false);
		address2.setActive(false);
		address1 = addressRepository.save(address1);
		address2 = addressRepository.save(address2);
		addressService.deleteMultiple(person.getId(), Arrays.asList(address1.getId(), address2.getId()));
		Assert.assertFalse(addressRepository.findById(address1.getId()).isPresent());
		Assert.assertFalse(addressRepository.findById(address2.getId()).isPresent());
		Address address = addressService.create(person.getId(), RandomUtils.randomAddress(person), allFieldFilter);
		address.setActive(false);
		address = addressRepository.save(address);
		addressService.deleteMultiple(person.getId(), Arrays.asList(address.getId(), 0L));
	}
}
