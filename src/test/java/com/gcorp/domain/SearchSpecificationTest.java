package com.gcorp.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.transaction.Transactional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.gcorp.ApiStarter;
import com.gcorp.common.Utils;
import com.gcorp.domain.SearchFilter.SearchFilterOperator;
import com.gcorp.enumeration.Gender;
import com.gcorp.enumeration.PhoneNumberType;
import com.gcorp.field.PhoneNumber;
import com.gcorp.notest.common.RandomUtils;
import com.gcorp.notest.config.H2Config;
import com.gcorp.notest.entity.Office;
import com.gcorp.notest.entity.Person;
import com.gcorp.notest.repository.AddressRepository;
import com.gcorp.notest.repository.OfficeRepository;
import com.gcorp.notest.repository.PersonRepository;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ApiStarter.class, H2Config.class })
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SearchSpecificationTest {

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

	@Before
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
	public void testFindByEqual() {
		final String personNameField = "person.name";
		final String name = "Name-1.Com";
		addressRepository.save(RandomUtils.randomAddress(persons.get(1)));
		Assert.assertEquals(1,
				addressRepository.findByFilters(getFilters(personNameField, SearchFilterOperator.IS_EQUAL, name), null)
						.getTotalElements());

		Assert.assertEquals(length - 1,
				personRepository.findByFilters(getFilters(NAME_FIELD, SearchFilterOperator.IS_NOT_EQUAL, name), null)
						.getTotalElements());
	}

	@Test
	public void testFindEqualsInsensitive() {
		final String personNameField = "person.name";
		final String nameInsensitive = "namE-1.com";
		addressRepository.save(RandomUtils.randomAddress(persons.get(1)));
		// FIXME: Implements insensitive lookup features
		Assert.assertEquals(0,
				addressRepository.findByFilters(
						getFilters(personNameField, SearchFilterOperator.IS_EQUAL_CASE_INSENSITIVE, nameInsensitive),
						null).getTotalElements());

		Assert.assertEquals(length,
				personRepository.findByFilters(
						getFilters(NAME_FIELD, SearchFilterOperator.IS_NOT_EQUAL_CASE_INSENSITIVE, nameInsensitive),
						null).getTotalElements());
	}

	@Test
	public void testFindByGreaterThan() {
		Assert.assertEquals(1,
				personRepository.findByFilters(getFilters(AGE_FIELD, SearchFilterOperator.IS_GREATER_THAN, 8), null)
						.getTotalElements());
		Assert.assertEquals(2,
				personRepository
						.findByFilters(getFilters(AGE_FIELD, SearchFilterOperator.IS_GREATER_THAN_OR_EQUAL, 8), null)
						.getTotalElements());
	}

	@Test
	public void testFindByLessThan() {
		Assert.assertEquals(8, personRepository
				.findByFilters(getFilters(AGE_FIELD, SearchFilterOperator.IS_LESS_THAN, 8), null).getTotalElements());
		Assert.assertEquals(9,
				personRepository
						.findByFilters(getFilters(AGE_FIELD, SearchFilterOperator.IS_LESS_THAN_OR_EQUAL, 8), null)
						.getTotalElements());
	}

	@Test
	public void testFindByNull() {
		// Null age
		personRepository.save(RandomUtils.randomPerson());
		Assert.assertEquals(1, personRepository
				.findByFilters(getFilters(AGE_FIELD, SearchFilterOperator.IS_NULL, null), null).getTotalElements());

		Assert.assertEquals(length, personRepository
				.findByFilters(getFilters(AGE_FIELD, SearchFilterOperator.IS_NOT_NULL, null), null).getTotalElements());
	}

	@Test
	public void testFindByLike() {
		final String nameLikeness = "ame-";
		Assert.assertEquals(persons.size(),
				personRepository.findByFilters(getFilters(NAME_FIELD, SearchFilterOperator.IS_LIKE, nameLikeness), null)
						.getTotalElements());
		Assert.assertEquals(length - persons.size(),
				personRepository
						.findByFilters(getFilters(NAME_FIELD, SearchFilterOperator.IS_NOT_LIKE, nameLikeness), null)
						.getTotalElements());

	}

	@Test
	public void testFindByStartingOrEnding() {
		Assert.assertEquals(persons.size(),
				personRepository
						.findByFilters(getFilters(NAME_FIELD, SearchFilterOperator.IS_STARTING_WITH, "Name"), null)
						.getTotalElements());
		Assert.assertEquals(persons.size(),
				personRepository
						.findByFilters(getFilters(NAME_FIELD, SearchFilterOperator.IS_ENDING_WITH, ".Com"), null)
						.getTotalElements());
	}

	@Test
	@Transactional
	public void testLocalDateTime() {
		Office office = officeRepository.save(RandomUtils.randomOffice());
		Assert.assertEquals(0, officeRepository.findByFilters(
				getFilters(CREATED_FIELD, SearchFilterOperator.IS_GREATER_THAN, office.getCreated().plusMinutes(1)),
				null).getTotalElements());
		Assert.assertTrue(1 <= officeRepository.findByFilters(getFilters(CREATED_FIELD,
				SearchFilterOperator.IS_GREATER_THAN_OR_EQUAL, office.getCreated().minusMinutes(1)), null)
				.getTotalElements());
		List<Long> idsLessThanOrEqual = officeRepository
				.findByFilters(getFilters(CREATED_FIELD, SearchFilterOperator.IS_LESS_THAN_OR_EQUAL,
						office.getCreated().plusMinutes(1)), null)
				.stream().map(Office::getId).collect(Collectors.toList());
		Assert.assertTrue(idsLessThanOrEqual.contains(office.getId()));
		List<Long> idsLess = officeRepository
				.findByFilters(getFilters(CREATED_FIELD, SearchFilterOperator.IS_LESS_THAN,
						office.getCreated().minusMinutes(1)), null)
				.stream().map(Office::getId).collect(Collectors.toList());
		Assert.assertFalse(idsLess.contains(office.getId()));
	}

	@Test
	@Transactional
	public void testEnum() {
		final String genderField = "gender";
		SearchFilters<Person> maleFilter = SearchFilters.of(genderField, SearchFilterOperator.IS_EQUAL, Gender.MALE);

		Page<Person> maleAccounts = personRepository.findByFilters(maleFilter, null);
		SearchFilters<Person> maleStringFilter = SearchFilters.of(genderField, SearchFilterOperator.IS_EQUAL, "MALE");
		Page<Person> maleStringAccounts = personRepository.findByFilters(maleStringFilter, null);
		Assert.assertEquals(maleAccounts.getTotalElements(), maleStringAccounts.getTotalElements());
	}

	@Test
	public void testFindByIsIn() {
		String[] names = { "Name-1.Com", "Name-2.Com" };
		String namesList = String.join(SearchSpecification.IS_IN_DELIMITER, names);
		final String nameField = "name";
		// 1 - Check if is in works
		long inNumber = personRepository
				.findByFilters(getFilters(nameField, SearchFilterOperator.IS_IN, namesList), null).getTotalElements();
		Assert.assertEquals(2, inNumber);

		// 2 - Check if is not in works
		long allPersons = personRepository
				.findByFilters(getFilters(nameField, SearchFilterOperator.IS_NOT_NULL, null), null).getTotalElements();
		long notPersons = personRepository
				.findByFilters(getFilters(nameField, SearchFilterOperator.IS_NOT_IN, namesList), null)
				.getTotalElements();
		Assert.assertEquals(notPersons, allPersons - 2);
	}

	@Test
	public void testDateTypeFields() {
		final String dateField = "expiry";
		final String timeField = "opening";
		Office office = officeRepository.save(RandomUtils.randomOffice());
		LocalDate today = LocalDate.now();
		LocalTime noon = LocalTime.NOON;

		// 1- Greater than
		Assert.assertNull(officeRepository.findOneByFilters(
				getFilters(CREATED_FIELD, SearchFilterOperator.IS_GREATER_THAN, office.getCreated())));
		Assert.assertNull(
				officeRepository.findOneByFilters(getFilters(dateField, SearchFilterOperator.IS_GREATER_THAN, today)));
		Assert.assertNull(
				officeRepository.findOneByFilters(getFilters(timeField, SearchFilterOperator.IS_GREATER_THAN, noon)));
		Assert.assertEquals(office.getId(),
				officeRepository.findOneByFilters(
						getFilters(CREATED_FIELD, SearchFilterOperator.IS_GREATER_THAN_OR_EQUAL, office.getCreated()))
						.getId());
		Assert.assertEquals(office.getId(), officeRepository
				.findOneByFilters(getFilters(dateField, SearchFilterOperator.IS_GREATER_THAN_OR_EQUAL, today)).getId());
		Assert.assertEquals(office.getId(), officeRepository
				.findOneByFilters(getFilters(timeField, SearchFilterOperator.IS_GREATER_THAN_OR_EQUAL, noon)).getId());

		// 2- Less than
		Assert.assertNull(officeRepository
				.findOneByFilters(getFilters(CREATED_FIELD, SearchFilterOperator.IS_LESS_THAN, office.getCreated())));
		Assert.assertNull(
				officeRepository.findOneByFilters(getFilters(dateField, SearchFilterOperator.IS_LESS_THAN, today)));
		Assert.assertNull(
				officeRepository.findOneByFilters(getFilters(timeField, SearchFilterOperator.IS_LESS_THAN, noon)));
		Assert.assertEquals(office.getId(),
				officeRepository.findOneByFilters(
						getFilters(CREATED_FIELD, SearchFilterOperator.IS_LESS_THAN_OR_EQUAL, office.getCreated()))
						.getId());
		Assert.assertEquals(office.getId(), officeRepository
				.findOneByFilters(getFilters(dateField, SearchFilterOperator.IS_LESS_THAN_OR_EQUAL, today)).getId());
		Assert.assertEquals(office.getId(), officeRepository
				.findOneByFilters(getFilters(timeField, SearchFilterOperator.IS_LESS_THAN_OR_EQUAL, noon)).getId());

	}

	@Test
	public void testCustomFields() {
		Office office = officeRepository.save(RandomUtils.randomOffice());
		// 1- Created
		Assert.assertNotNull(officeRepository
				.findOneByFilters(getFilters(CREATED_FIELD, SearchFilterOperator.IS_EQUAL, office.getCreated())));
		ZonedDateTime dateTime = ZonedDateTime.of(office.getCreated(), ZoneId.of(Utils.UTC_ZONE));
		Assert.assertNotNull(officeRepository.findOneByFilters(getFilters(CREATED_FIELD, SearchFilterOperator.IS_EQUAL,
				dateTime.format(DateTimeFormatter.ofPattern(Utils.API_DATETIME_FORMAT)))));

		// 2- Country
		Assert.assertNotNull(officeRepository
				.findOneByFilters(getFilters(COUNTRY_FIELD, SearchFilterOperator.IS_EQUAL, office.getCountry())));
		Assert.assertNotNull(officeRepository.findOneByFilters(
				getFilters(COUNTRY_FIELD, SearchFilterOperator.IS_EQUAL, office.getCountry().getCode())));

		// 3- MoneyCurrency
		Assert.assertNotNull(officeRepository
				.findOneByFilters(getFilters(CURRENCY_FIELD, SearchFilterOperator.IS_EQUAL, office.getCurrency())));
		Assert.assertNotNull(officeRepository.findOneByFilters(
				getFilters(CURRENCY_FIELD, SearchFilterOperator.IS_EQUAL, office.getCurrency().getCode())));

		// 4- FaxNumber
		Assert.assertNotNull(officeRepository
				.findOneByFilters(getFilters(FAX_FIELD, SearchFilterOperator.IS_EQUAL, office.getFax())));
		Assert.assertNotNull(officeRepository
				.findOneByFilters(getFilters(FAX_FIELD, SearchFilterOperator.IS_EQUAL, office.getFax().toString())));

		// 5- HomeNumber
		Assert.assertNotNull(officeRepository
				.findOneByFilters(getFilters(HOME_FIELD, SearchFilterOperator.IS_EQUAL, office.getHome())));
		Assert.assertNotNull(officeRepository
				.findOneByFilters(getFilters(HOME_FIELD, SearchFilterOperator.IS_EQUAL, office.getHome().toString())));

		// 6- MobileNumber
		Assert.assertNotNull(officeRepository
				.findOneByFilters(getFilters(MOBILE_FIELD, SearchFilterOperator.IS_EQUAL, office.getMobile())));
		Assert.assertNotNull(officeRepository.findOneByFilters(
				getFilters(MOBILE_FIELD, SearchFilterOperator.IS_EQUAL, office.getMobile().toString())));
		PhoneNumber phoneNumber = PhoneNumber.newPhoneNumber(PhoneNumberType.MOBILE, office.getMobile().getAreaCode(),
				office.getMobile().getExtension(), office.getMobile().getPrefix(), office.getMobile().getSuffix());
		Assert.assertNotNull(officeRepository
				.findOneByFilters(getFilters(MOBILE_FIELD, SearchFilterOperator.IS_EQUAL, phoneNumber)));

		// 7- Expiry
		Assert.assertNotNull(officeRepository
				.findOneByFilters(getFilters(EXPIRY_FIELD, SearchFilterOperator.IS_EQUAL, office.getExpiry())));
		Assert.assertNotNull(officeRepository.findOneByFilters(getFilters(EXPIRY_FIELD, SearchFilterOperator.IS_EQUAL,
				office.getExpiry().format(DateTimeFormatter.ofPattern(Utils.API_DATE_FORMAT)))));

		// 8- Opening
		Assert.assertNotNull(officeRepository
				.findOneByFilters(getFilters(OPENING_FIELD, SearchFilterOperator.IS_EQUAL, office.getOpening())));
		Assert.assertNotNull(officeRepository.findOneByFilters(getFilters(OPENING_FIELD, SearchFilterOperator.IS_EQUAL,
				office.getOpening().format(DateTimeFormatter.ofPattern(Utils.API_TIME_FORMAT)))));
	}

	public void testSafeFiltersValue() {

	}

}
