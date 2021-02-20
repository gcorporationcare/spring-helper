package com.gcorp.domain;

import java.util.HashSet;

import org.junit.Assert;
import org.junit.Test;

import com.gcorp.notest.Person;
import com.gcorp.notest.RandomUtils;

public class FieldFilterTest {

	@Test
	public void testReadDefaultFields() {
		Person person = new Person("Any name", RandomUtils.randomEmail(), "en", new int[] { 1, 2 },
				new Double[] { 1.2, 2.3 }, new String[] { "add", "sub" }, new HashSet<>());
		Person defaultPerson = FieldFilter.<Person>fromString(null).parseEntity(person);
		Assert.assertNotNull(defaultPerson.getEmail());
		Assert.assertNull(defaultPerson.getLanguage());

		Person defaultPlusPerson = FieldFilter.<Person>fromString("+language").parseEntity(person);
		// No more null since it has been added
		Assert.assertNotNull(defaultPlusPerson.getLanguage());
		Assert.assertNotNull(defaultPlusPerson.getEmail());
	}

	@Test
	public void testReadAllFields() {
//		Account account = Account.builder().person(Person.anonymous()).lastLoginDate(LocalDateTime.now())
//				.password("alphabet").build();
//		Account allExceptAccount = FieldFilter.<Account>fromString("-lastLoginDate").parseEntity(account);
//		Assert.assertNotNull(allExceptAccount.getEmail());
//		// Role has been removed
//		Assert.assertNull(allExceptAccount.getLastLoginDate());
//		// Password should never be serialized
//		Assert.assertNull(allExceptAccount.getPassword());
//
//		Account allAccount = FieldFilter.<Account>allFields().parseEntity(account);
//		// Role is now present
//		Assert.assertNull(allAccount.getRole());
//		// Never ever be serialized
//		// TODO: Remove this comment to try out if something must be done
//		// Assert.assertNull(allAccount.getPassword());
	}

	@Test
	public void testReadCustomFields() {
		Person person = new Person();
		person.setEmail(RandomUtils.randomEmail());
		person.setLanguage("alpha");
		person.setName("Alphabet");
		// Even with typo, name is a minimal field (never omitted)
		Person customPerson = FieldFilter.<Person>fromString("languaage,naame").parseEntity(person);
		// Email is minimal field so will never be omitted
		Assert.assertNotNull(customPerson.getEmail());
		Assert.assertNotNull(customPerson.getName());
		Assert.assertNull(customPerson.getLanguage());
	}
}
