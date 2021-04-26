package com.github.gcorporationcare.notest.common;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;

import com.github.gcorporationcare.data.field.Country;
import com.github.gcorporationcare.notest.entity.Address;
import com.github.gcorporationcare.notest.entity.Person;
import com.github.gcorporationcare.notest.entity.PersonTag;
import com.github.gcorporationcare.notest.entity.PersonTagAuthor;
import com.github.gcorporationcare.notest.entity.PersonTagDescription;
import com.github.gcorporationcare.notest.enumeration.Gender;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Useful for generating random values of existing types
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RandomUtils {

	private static final int DEFAULT_MAX_SIZE = 100;

	public static String randomString(int count) {
		String value = RandomStringUtils.randomAlphabetic(count);
		return value != null ? value : UUID.randomUUID().toString().substring(count);
	}

	public static String randomString() {
		return randomString(Math.abs((new SecureRandom()).nextInt(DEFAULT_MAX_SIZE)));
	}

	public static String randomEmail(int count) {
		int length = count >= 6 ? (count - 4) / 2 : 3;
		return String.format("%s@%s.com", randomString(length), randomString(length));
	}

	public static String randomEmail() {
		return randomEmail(Math.abs((new SecureRandom()).nextInt(DEFAULT_MAX_SIZE)));
	}

	public static int randomInteger() {
		return (new SecureRandom()).nextInt();
	}

	public static int randomInteger(int max) {
		return (new SecureRandom()).nextInt(max);
	}

	public static double randomDouble() {
		return (new SecureRandom()).nextDouble();
	}

	public static boolean randomBoolean() {
		return (new SecureRandom()).nextBoolean();
	}

	public static Person randomPerson() {
		Gender[] genders = Gender.values();
		return new Person(RandomUtils.randomString(10), RandomUtils.randomEmail(), "en",
				genders[RandomUtils.randomInteger(1)], null, null, new int[] { 1, 2 }, new Double[] { 1.2, 2.3 },
				new String[] { "add", "sub" }, new HashSet<>());
	}

	public static PersonTag randomPersonTag(Person person, Country country) {
		PersonTagAuthor author = new PersonTagAuthor("John", "DOE", country);
		PersonTagDescription description = new PersonTagDescription("A title", "My description", author);
		return new PersonTag("Simple tag", person.getId(), description);
	}

	public static Address randomAddress(Person person) {
		Person owner = person == null ? randomPerson() : person;
		return new Address("Personal address", "A given street", "12345", "Code-City", "Test-State", true, owner);
	}
}
