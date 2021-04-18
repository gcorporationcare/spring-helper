package com.github.gcorporationcare.data.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import com.github.gcorporationcare.ApiStarter;
import com.github.gcorporationcare.data.domain.PropertyPath;
import com.github.gcorporationcare.data.domain.SearchFilter;
import com.github.gcorporationcare.data.domain.SearchFilter.SearchFilterOperator;
import com.github.gcorporationcare.data.domain.SearchFilters;
import com.github.gcorporationcare.notest.common.RandomUtils;
import com.github.gcorporationcare.notest.config.H2Config;
import com.github.gcorporationcare.notest.entity.Person;
import com.github.gcorporationcare.notest.enumeration.Gender;
import com.github.gcorporationcare.notest.repository.PersonRepository;

@ActiveProfiles("test")
@SpringBootTest(classes = { ApiStarter.class, H2Config.class })
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class BaseRepositoryTest {
	@PersistenceContext
	EntityManager entityManager;
	@Autowired
	PersonRepository personRepository;

	@Test
	void testSorting() {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Person> query = builder.createQuery(Person.class);
		Root<Person> root = query.from(Person.class);
		Order order = builder.desc(PropertyPath.getParametizedPath(root, "name"));
		query.orderBy(new Order[] { order });
		assertEquals(1, query.getOrderList().size());
	}

	@Test
	void testFindFilters() {
		Pageable pageable = PageRequest.of(0, 1);
		personRepository.save(RandomUtils.randomPerson());
		SearchFilters<Person> searchFilters = null;
		String searchFilterString = "id,!0";
		assertEquals(1, personRepository.findByFilters(searchFilters, pageable).getTotalElements());
		assertEquals(1, personRepository.findByFilters(searchFilterString, null).getTotalElements());
		assertNotNull(personRepository.findOneByFilters(searchFilterString));
	}

	@Test
	void testSimpleRepository() {
		Person person = RandomUtils.randomPerson();
		person.setGender(Gender.FEMALE);
		person = personRepository.save(person);
		assertNotNull(person.getId());
		SearchFilters<Person> filters = SearchFilters.of("email", SearchFilterOperator.IS_EQUAL, person.getEmail());
		assertEquals(1, personRepository.findByFilters(filters, null).getTotalElements());
		final String genderField = "gender";
		filters.and(new SearchFilter(true, genderField, SearchFilterOperator.IS_EQUAL, Gender.FEMALE));
		assertEquals(1, personRepository.findByFilters(filters, null).getTotalElements());
		filters.and(new SearchFilter(true, genderField, SearchFilterOperator.IS_EQUAL, Gender.MALE));
		assertEquals(0, personRepository.findByFilters(filters, null).getTotalElements());
	}
}
