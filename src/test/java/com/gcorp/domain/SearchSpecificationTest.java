package com.gcorp.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.gcorp.ApiStarter;
import com.gcorp.domain.SearchFilter.SearchFilterOperator;
import com.gcorp.notest.common.RandomUtils;
import com.gcorp.notest.config.H2Config;
import com.gcorp.notest.entity.Person;
import com.gcorp.notest.repository.AddressRepository;
import com.gcorp.notest.repository.PersonRepository;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ApiStarter.class, H2Config.class })
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SearchSpecificationTest {

	private static final String ID_FIELD = "id";
	private long length;
	private List<Person> persons = new ArrayList<>();

	@Autowired
	PersonRepository personRepository;
	@Autowired
	AddressRepository addressRepository;

	@Before
	public void setUpSomeData() {
		IntStream.range(0, 10).forEach(i -> {
			Person person = RandomUtils.randomPerson();
			person.setName(String.format("name-%d", i));
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
		final String name = "Name-1";
		addressRepository.save(RandomUtils.randomAddress(persons.get(1)));
		Assert.assertEquals(1,
				addressRepository.findByFilters(getFilters("person.name", SearchFilterOperator.IS_EQUAL, name), null)
						.getTotalElements());

		Assert.assertEquals(length - 1, personRepository
				.findByFilters(getFilters("name", SearchFilterOperator.IS_NOT_EQUAL, name), null).getTotalElements());
	}

	@Test
	public void testFindByGreaterThan() {
		Assert.assertTrue(
				personRepository.findByFilters(getFilters(ID_FIELD, SearchFilterOperator.IS_GREATER_THAN, 1), null)
						.getTotalElements() > 0);
		Assert.assertTrue(personRepository
				.findByFilters(getFilters(ID_FIELD, SearchFilterOperator.IS_GREATER_THAN_OR_EQUAL, 2), null)
				.getTotalElements() > 0);
	}

	@Test
	public void testFindByLessThan() {
		Assert.assertTrue(
				personRepository.findByFilters(getFilters(ID_FIELD, SearchFilterOperator.IS_LESS_THAN, 3), null)
						.getTotalElements() > 0);
	}

}
