package com.gcorp.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.gcorp.ApiStarter;
import com.gcorp.domain.PropertyPath;
import com.gcorp.domain.SearchFilter;
import com.gcorp.domain.SearchFilter.SearchFilterOperator;
import com.gcorp.domain.SearchFilters;
import com.gcorp.enumeration.Gender;
import com.gcorp.notest.common.RandomUtils;
import com.gcorp.notest.config.H2Config;
import com.gcorp.notest.entity.Person;
import com.gcorp.notest.repository.PersonRepository;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ApiStarter.class, H2Config.class })
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BaseRepositoryTest {
	@PersistenceContext
	EntityManager entityManager;
	@Autowired
	PersonRepository personRepository;

	@Test
	public void testSorting() {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Person> query = builder.createQuery(Person.class);
		Root<Person> root = query.from(Person.class);
		Order order = builder.desc(PropertyPath.getParametizedPath(root, "name"));
		query.orderBy(new Order[] { order });
		Assert.assertEquals(1, query.getOrderList().size());
	}

	@Test
	public void testFindFilters() {
		Pageable pageable = PageRequest.of(0, 1);
		personRepository.save(RandomUtils.randomPerson());
		SearchFilters<Person> searchFilters = null;
		String searchFilterString = "id,!0";
		Assert.assertEquals(1, personRepository.findByFilters(searchFilters, pageable).getTotalElements());
		Assert.assertEquals(1, personRepository.findByFilters(searchFilterString, null).getTotalElements());
		Assert.assertNotNull(personRepository.findOneByFilters(searchFilterString));
	}

	@Test
	public void testSimpleRepository() {
		Person person = RandomUtils.randomPerson();
		person.setGender(Gender.FEMALE);
		person = personRepository.save(person);
		Assert.assertNotNull(person.getId());
		SearchFilters<Person> filters = SearchFilters.of("email", SearchFilterOperator.IS_EQUAL, person.getEmail());
		Assert.assertEquals(1, personRepository.findByFilters(filters, null).getTotalElements());
		final String genderField = "gender";
		filters.and(new SearchFilter(true, genderField, SearchFilterOperator.IS_EQUAL, Gender.FEMALE));
		Assert.assertEquals(1, personRepository.findByFilters(filters, null).getTotalElements());
		filters.and(new SearchFilter(true, genderField, SearchFilterOperator.IS_EQUAL, Gender.MALE));
		Assert.assertEquals(0, personRepository.findByFilters(filters, null).getTotalElements());
	}
}
