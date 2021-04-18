package com.github.gcorporationcare.data.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import com.github.gcorporationcare.ApiStarter;
import com.github.gcorporationcare.data.common.Utils;
import com.github.gcorporationcare.data.domain.SearchFilter.SearchFilterOperator;
import com.github.gcorporationcare.data.enumeration.Gender;
import com.github.gcorporationcare.data.enumeration.PhoneNumberType;
import com.github.gcorporationcare.data.field.PhoneNumber;
import com.github.gcorporationcare.notest.common.RandomUtils;
import com.github.gcorporationcare.notest.config.H2Config;
import com.github.gcorporationcare.notest.entity.Office;
import com.github.gcorporationcare.notest.entity.Person;
import com.github.gcorporationcare.notest.repository.AddressRepository;
import com.github.gcorporationcare.notest.repository.OfficeRepository;
import com.github.gcorporationcare.notest.repository.PersonRepository;

@ActiveProfiles("test")
@SpringBootTest(classes = { ApiStarter.class, H2Config.class })
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class SearchSpecificationTest {

	private static final String AGE_FIELD = "age";
	private static final String NAME_FIELD = "name";
	private static final String CREATED_FIELD = "created";
	private static final String COUNTRY_FIELD = "country";
	private static final String CURRENCY_FIELD = "currency";
	private static final String FAX_FIELD = "fax";
	private static final String HOME_FIELD = "home";
	private static final String MOBILE_FIELD = "mobile";
	private static final String EXPIRY_FIELD = "expiry";
	private static final String OPENING_FIELD = "opening";

	private long length;
	private List<Person> persons = new ArrayList<>();

	@Autowired
	PersonRepository personRepository;
	@Autowired
	AddressRepository addressRepository;
	@Autowired
	OfficeRepository officeRepository;

	@BeforeEach
	public void setUpSomeData() {
		IntStream.range(0, 10).forEach(i -> {
			Person person = RandomUtils.randomPerson();
			person.setName(String.format("name-%d.com", i));
			person.setAge(i);
			persons.add(person);
		});
		persons = (List<Person>) personRepository.saveAll(persons);
		length = personRepository.count();
	}

	private static <T> SearchFilters<T> getFilters(String field, SearchFilterOperator operator, Serializable value) {
		return SearchFilters.of(field, operator, value);
	}

	@Test
	void testFindByEqual() {
		final String personNameField = "person.name";
		final String name = "Name-1.Com";
		addressRepository.save(RandomUtils.randomAddress(persons.get(1)));
		assertEquals(1,
				addressRepository.findByFilters(getFilters(personNameField, SearchFilterOperator.IS_EQUAL, name), null)
						.getTotalElements());

		assertEquals(length - 1,
				personRepository.findByFilters(getFilters(NAME_FIELD, SearchFilterOperator.IS_NOT_EQUAL, name), null)
						.getTotalElements());
	}

	@Test
	void testFindEqualsInsensitive() {
		final String personNameField = "person.name";
		final String nameInsensitive = "namE-1.com";
		addressRepository.save(RandomUtils.randomAddress(persons.get(1)));
		// FIXME: Implements insensitive lookup features
		assertEquals(0,
				addressRepository.findByFilters(
						getFilters(personNameField, SearchFilterOperator.IS_EQUAL_CASE_INSENSITIVE, nameInsensitive),
						null).getTotalElements());

		assertEquals(length,
				personRepository.findByFilters(
						getFilters(NAME_FIELD, SearchFilterOperator.IS_NOT_EQUAL_CASE_INSENSITIVE, nameInsensitive),
						null).getTotalElements());
	}

	@Test
	void testFindByGreaterThan() {
		assertEquals(1,
				personRepository.findByFilters(getFilters(AGE_FIELD, SearchFilterOperator.IS_GREATER_THAN, 8), null)
						.getTotalElements());
		assertEquals(2,
				personRepository
						.findByFilters(getFilters(AGE_FIELD, SearchFilterOperator.IS_GREATER_THAN_OR_EQUAL, 8), null)
						.getTotalElements());
	}

	@Test
	void testFindByLessThan() {
		assertEquals(8, personRepository
				.findByFilters(getFilters(AGE_FIELD, SearchFilterOperator.IS_LESS_THAN, 8), null).getTotalElements());
		assertEquals(9,
				personRepository
						.findByFilters(getFilters(AGE_FIELD, SearchFilterOperator.IS_LESS_THAN_OR_EQUAL, 8), null)
						.getTotalElements());
	}

	@Test
	void testFindByNull() {
		// Null age
		personRepository.save(RandomUtils.randomPerson());
		assertEquals(1, personRepository.findByFilters(getFilters(AGE_FIELD, SearchFilterOperator.IS_NULL, null), null)
				.getTotalElements());

		assertEquals(length, personRepository
				.findByFilters(getFilters(AGE_FIELD, SearchFilterOperator.IS_NOT_NULL, null), null).getTotalElements());
	}

	@Test
	void testFindByLike() {
		final String nameLikeness = "ame-";
		assertEquals(persons.size(),
				personRepository.findByFilters(getFilters(NAME_FIELD, SearchFilterOperator.IS_LIKE, nameLikeness), null)
						.getTotalElements());
		assertEquals(length - persons.size(),
				personRepository
						.findByFilters(getFilters(NAME_FIELD, SearchFilterOperator.IS_NOT_LIKE, nameLikeness), null)
						.getTotalElements());

	}

	@Test
	void testFindByStartingOrEnding() {
		assertEquals(persons.size(),
				personRepository
						.findByFilters(getFilters(NAME_FIELD, SearchFilterOperator.IS_STARTING_WITH, "Name"), null)
						.getTotalElements());
		assertEquals(persons.size(),
				personRepository
						.findByFilters(getFilters(NAME_FIELD, SearchFilterOperator.IS_ENDING_WITH, ".Com"), null)
						.getTotalElements());
	}

	@Test
	@Transactional
	void testLocalDateTime() {
		Office office = officeRepository.save(RandomUtils.randomOffice());
		assertEquals(0, officeRepository.findByFilters(
				getFilters(CREATED_FIELD, SearchFilterOperator.IS_GREATER_THAN, office.getCreated().plusMinutes(1)),
				null).getTotalElements());
		assertTrue(1 <= officeRepository.findByFilters(getFilters(CREATED_FIELD,
				SearchFilterOperator.IS_GREATER_THAN_OR_EQUAL, office.getCreated().minusMinutes(1)), null)
				.getTotalElements());
		List<Long> idsLessThanOrEqual = officeRepository
				.findByFilters(getFilters(CREATED_FIELD, SearchFilterOperator.IS_LESS_THAN_OR_EQUAL,
						office.getCreated().plusMinutes(1)), null)
				.stream().map(Office::getId).collect(Collectors.toList());
		assertTrue(idsLessThanOrEqual.contains(office.getId()));
		List<Long> idsLess = officeRepository
				.findByFilters(getFilters(CREATED_FIELD, SearchFilterOperator.IS_LESS_THAN,
						office.getCreated().minusMinutes(1)), null)
				.stream().map(Office::getId).collect(Collectors.toList());
		assertFalse(idsLess.contains(office.getId()));
	}

	@Test
	@Transactional
	void testEnum() {
		final String genderField = "gender";
		SearchFilters<Person> maleFilter = SearchFilters.of(genderField, SearchFilterOperator.IS_EQUAL, Gender.MALE);

		Page<Person> maleAccounts = personRepository.findByFilters(maleFilter, null);
		SearchFilters<Person> maleStringFilter = SearchFilters.of(genderField, SearchFilterOperator.IS_EQUAL, "MALE");
		Page<Person> maleStringAccounts = personRepository.findByFilters(maleStringFilter, null);
		assertEquals(maleAccounts.getTotalElements(), maleStringAccounts.getTotalElements());
	}

	@Test
	void testFindByIsIn() {
		String[] names = { "Name-1.Com", "Name-2.Com" };
		String namesList = String.join(SearchSpecification.IS_IN_DELIMITER, names);
		final String nameField = "name";
		// 1 - Check if is in works
		long inNumber = personRepository
				.findByFilters(getFilters(nameField, SearchFilterOperator.IS_IN, namesList), null).getTotalElements();
		assertEquals(2, inNumber);

		// 2 - Check if is not in works
		long allPersons = personRepository
				.findByFilters(getFilters(nameField, SearchFilterOperator.IS_NOT_NULL, null), null).getTotalElements();
		long notPersons = personRepository
				.findByFilters(getFilters(nameField, SearchFilterOperator.IS_NOT_IN, namesList), null)
				.getTotalElements();
		assertEquals(notPersons, allPersons - 2);
	}

	@Test
	void testDateTypeFields() {
		final String dateField = "expiry";
		final String timeField = "opening";
		Office office = officeRepository.save(RandomUtils.randomOffice());
		office = officeRepository.findById(office.getId()).get();
		LocalDate today = LocalDate.now();
		LocalTime noon = LocalTime.NOON;

		// 1- Greater than
		LocalDateTime createdAfter = office.getCreated().plusSeconds(30);
		assertNull(officeRepository
				.findOneByFilters(getFilters(CREATED_FIELD, SearchFilterOperator.IS_GREATER_THAN, createdAfter)));
		assertNull(
				officeRepository.findOneByFilters(getFilters(dateField, SearchFilterOperator.IS_GREATER_THAN, today)));
		assertNull(
				officeRepository.findOneByFilters(getFilters(timeField, SearchFilterOperator.IS_GREATER_THAN, noon)));
		assertEquals(office.getId(),
				officeRepository.findOneByFilters(
						getFilters(CREATED_FIELD, SearchFilterOperator.IS_GREATER_THAN_OR_EQUAL, office.getCreated()))
						.getId());
		assertEquals(office.getId(), officeRepository
				.findOneByFilters(getFilters(dateField, SearchFilterOperator.IS_GREATER_THAN_OR_EQUAL, today)).getId());
		assertEquals(office.getId(), officeRepository
				.findOneByFilters(getFilters(timeField, SearchFilterOperator.IS_GREATER_THAN_OR_EQUAL, noon)).getId());

		// 2- Less than
		assertNull(officeRepository
				.findOneByFilters(getFilters(CREATED_FIELD, SearchFilterOperator.IS_LESS_THAN, office.getCreated())));
		assertNull(officeRepository.findOneByFilters(getFilters(dateField, SearchFilterOperator.IS_LESS_THAN, today)));
		assertNull(officeRepository.findOneByFilters(getFilters(timeField, SearchFilterOperator.IS_LESS_THAN, noon)));
		assertEquals(office.getId(),
				officeRepository.findOneByFilters(
						getFilters(CREATED_FIELD, SearchFilterOperator.IS_LESS_THAN_OR_EQUAL, office.getCreated()))
						.getId());
		assertEquals(office.getId(), officeRepository
				.findOneByFilters(getFilters(dateField, SearchFilterOperator.IS_LESS_THAN_OR_EQUAL, today)).getId());
		assertEquals(office.getId(), officeRepository
				.findOneByFilters(getFilters(timeField, SearchFilterOperator.IS_LESS_THAN_OR_EQUAL, noon)).getId());
	}

	@Test
	void testCustomFields() {
		Office office = officeRepository.save(RandomUtils.randomOffice());
		// In order to avoid issue with millisecond not being the same before and after
		// save
		office = officeRepository.findById(office.getId()).get();
		// 1- Created
		assertNotNull(officeRepository
				.findOneByFilters(getFilters(CREATED_FIELD, SearchFilterOperator.IS_EQUAL, office.getCreated())));
		ZonedDateTime dateTime = ZonedDateTime.of(office.getCreated(), ZoneId.of(Utils.UTC_ZONE));
		assertNotNull(officeRepository
				.findOneByFilters(getFilters(CREATED_FIELD, SearchFilterOperator.IS_GREATER_THAN_OR_EQUAL,
						dateTime.format(DateTimeFormatter.ofPattern(Utils.API_DATETIME_FORMAT)))));

		// 2- Country
		assertNotNull(officeRepository
				.findOneByFilters(getFilters(COUNTRY_FIELD, SearchFilterOperator.IS_EQUAL, office.getCountry())));
		assertNotNull(officeRepository.findOneByFilters(
				getFilters(COUNTRY_FIELD, SearchFilterOperator.IS_EQUAL, office.getCountry().getCode())));

		// 3- MoneyCurrency
		assertNotNull(officeRepository
				.findOneByFilters(getFilters(CURRENCY_FIELD, SearchFilterOperator.IS_EQUAL, office.getCurrency())));
		assertNotNull(officeRepository.findOneByFilters(
				getFilters(CURRENCY_FIELD, SearchFilterOperator.IS_EQUAL, office.getCurrency().getCode())));

		// 4- FaxNumber
		assertNotNull(officeRepository
				.findOneByFilters(getFilters(FAX_FIELD, SearchFilterOperator.IS_EQUAL, office.getFax())));
		assertNotNull(officeRepository
				.findOneByFilters(getFilters(FAX_FIELD, SearchFilterOperator.IS_EQUAL, office.getFax().toString())));

		// 5- HomeNumber
		assertNotNull(officeRepository
				.findOneByFilters(getFilters(HOME_FIELD, SearchFilterOperator.IS_EQUAL, office.getHome())));
		assertNotNull(officeRepository
				.findOneByFilters(getFilters(HOME_FIELD, SearchFilterOperator.IS_EQUAL, office.getHome().toString())));

		// 6- MobileNumber
		assertNotNull(officeRepository
				.findOneByFilters(getFilters(MOBILE_FIELD, SearchFilterOperator.IS_EQUAL, office.getMobile())));
		assertNotNull(officeRepository.findOneByFilters(
				getFilters(MOBILE_FIELD, SearchFilterOperator.IS_EQUAL, office.getMobile().toString())));
		PhoneNumber phoneNumber = PhoneNumber.newPhoneNumber(PhoneNumberType.MOBILE, office.getMobile().getAreaCode(),
				office.getMobile().getExtension(), office.getMobile().getPrefix(), office.getMobile().getSuffix());
		assertNotNull(officeRepository
				.findOneByFilters(getFilters(MOBILE_FIELD, SearchFilterOperator.IS_EQUAL, phoneNumber)));

		// 7- Expiry
		assertNotNull(officeRepository
				.findOneByFilters(getFilters(EXPIRY_FIELD, SearchFilterOperator.IS_EQUAL, office.getExpiry())));
		assertNotNull(officeRepository.findOneByFilters(getFilters(EXPIRY_FIELD, SearchFilterOperator.IS_EQUAL,
				office.getExpiry().format(DateTimeFormatter.ofPattern(Utils.API_DATE_FORMAT)))));

		// 8- Opening
		assertNotNull(officeRepository
				.findOneByFilters(getFilters(OPENING_FIELD, SearchFilterOperator.IS_EQUAL, office.getOpening())));
		assertNotNull(officeRepository.findOneByFilters(getFilters(OPENING_FIELD, SearchFilterOperator.IS_EQUAL,
				office.getOpening().format(DateTimeFormatter.ofPattern(Utils.API_TIME_FORMAT)))));
	}

	void testSafeFiltersValue() {

	}

}
