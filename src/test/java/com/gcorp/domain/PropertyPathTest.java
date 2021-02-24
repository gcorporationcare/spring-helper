package com.gcorp.domain;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.gcorp.ApiStarter;
import com.gcorp.notest.config.H2Config;
import com.gcorp.notest.entity.Address;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ApiStarter.class, H2Config.class })
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PropertyPathTest {
	Root<Address> root;
	CriteriaBuilder builder;
	CriteriaQuery<Address> query;
	@PersistenceContext
	EntityManager entityManager;

	@Before
	public void setUp() {
		builder = entityManager.getCriteriaBuilder();
		query = builder.createQuery(Address.class);
		root = query.from(Address.class);
	}

	@Test
	public void testGetJoin() {
		Assert.assertNotNull(PropertyPath.getJoin(root, "person.email"));
		Assert.assertNotNull(PropertyPath.getJoin(root, "person.parent.email"));
	}

	@Test
	public void testGetPath() {
		Assert.assertNotNull(PropertyPath.getPath(root, "person.email"));
	}

	@Test
	public void testGetParametizedPath() {
		Assert.assertNotNull(PropertyPath.getParametizedPath(root, "street"));
		Assert.assertNotNull(PropertyPath.getParametizedPath(root, "person.email"));
	}
}
