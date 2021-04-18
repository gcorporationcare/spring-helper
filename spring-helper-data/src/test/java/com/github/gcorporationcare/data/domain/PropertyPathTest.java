package com.github.gcorporationcare.data.domain;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import com.github.gcorporationcare.ApiStarter;
import com.github.gcorporationcare.notest.config.H2Config;
import com.github.gcorporationcare.notest.entity.Address;

@ActiveProfiles("test")
@SpringBootTest(classes = { ApiStarter.class, H2Config.class })
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class PropertyPathTest {
	Root<Address> root;
	CriteriaBuilder builder;
	CriteriaQuery<Address> query;
	@PersistenceContext
	EntityManager entityManager;

	@BeforeEach
	public void setUp() {
		builder = entityManager.getCriteriaBuilder();
		query = builder.createQuery(Address.class);
		root = query.from(Address.class);
	}

	@Test
	void testGetJoin() {
		assertNotNull(PropertyPath.getJoin(root, "person.email"));
		assertNotNull(PropertyPath.getJoin(root, "person.parent.email"));
	}

	@Test
	void testGetPath() {
		assertNotNull(PropertyPath.getPath(root, "person.email"));
	}

	@Test
	void testGetParametizedPath() {
		assertNotNull(PropertyPath.getParametizedPath(root, "street"));
		assertNotNull(PropertyPath.getParametizedPath(root, "person.email"));
	}
}
