package com.gcorp.repository;

import java.util.HashSet;

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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.gcorp.ApiStarter;
import com.gcorp.domain.PropertyPath;
import com.gcorp.domain.SearchFilter.SearchFilterOperator;
import com.gcorp.domain.SearchFilters;
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
	public void testSimpleRepository() {
		Person person = new Person(null, "Any name", RandomUtils.randomEmail(), "en", new int[] { 1, 2 },
				new Double[] { 1.2, 2.3 }, new String[] { "add", "sub" }, new HashSet<>());
		person = personRepository.save(person);
		Assert.assertNotNull(person.getId());
		Assert.assertEquals(1, personRepository
				.findByFilters(SearchFilters.of("email", SearchFilterOperator.IS_EQUAL, person.getEmail()), null)
				.getTotalElements());
	}
}
