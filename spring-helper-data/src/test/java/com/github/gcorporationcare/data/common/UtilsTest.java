package com.github.gcorporationcare.data.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import javax.validation.constraints.Email;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Test;

import com.github.gcorporationcare.data.entity.BaseEntity;
import com.github.gcorporationcare.data.exception.StandardRuntimeException;
import com.github.gcorporationcare.notest.common.RandomUtils;
import com.github.gcorporationcare.notest.entity.Person;

class UtilsTest {
	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	@interface SimpleAnnotation {

	}

	@SimpleAnnotation
	class GenericType<T> {
		String field1;
		String field2;

		String who() {
			return this.getClass().getName();
		}
	}

	@SimpleAnnotation
	class SimpleType extends GenericType<String> {
		String field3;
	}

	@Test
	void testGetParameterizedType() {
		assertEquals(String.class, Utils.getParameterizedType(SimpleType.class));
	}

	@Test
	void testGetParameterizedType_WithNoArgument() {
		assertThrows(NullPointerException.class, () -> Utils.getParameterizedType(null));
	}

	@Test
	void testGetParameterizedType_WithNoGenericClass() {
		assertThrows(IllegalArgumentException.class, () -> Utils.getParameterizedType(String.class));
	}

	@Test
	void testGetNullPropertyNames() {
		Person person = new Person();
		String[] nullProperties = Utils.getNullPropertyNames(person);
		assertNotNull(nullProperties);
		assertTrue(ArrayUtils.contains(nullProperties, "name"));
		person.setName("A given name");
		assertEquals(nullProperties.length - 1, Utils.getNullPropertyNames(person).length);
	}

	@Test
	void testGetFieldValue() {
		final String nameField = "name";
		Person person = new Person();
		person.setName("A given name");
		assertEquals(person.getName(), Utils.getFieldValue(nameField, person, Person.class));
		assertEquals(person.getName(), Utils.getFieldValue(nameField, person, BaseEntity.class));
	}

	@Test
	void testGetFieldValue_WithBadClass() {
		assertThrows(StandardRuntimeException.class,
				() -> Utils.getFieldValue("unknown field", "Alphabet", Person.class));
	}

	@Test
	void testGetFieldValue_WithUnknownField() {
		Person person = new Person();
		assertThrows(StandardRuntimeException.class, () -> Utils.getFieldValue("weird field", person, Person.class));
	}

	@Test
	void testGetInheritedField() {
		assertNotNull(Utils.getInheritedField("created", Person.class, BaseEntity.class));
	}

	@Test
	void testGetInheritedFields() {
		assertFalse(Utils.getInheritedFields(Person.class, BaseEntity.class).isEmpty());
	}

	@Test
	void testGetInheritedFields_WithBadSuperType() {
		assertTrue(Utils.getInheritedFields(Person.class, String.class).isEmpty());
	}

	@Test
	void testGetFieldValue_WithNotDeepSuperClass() {
		Person person = new Person();
		assertThrows(StandardRuntimeException.class, () -> Utils.getFieldValue("createdBy", person, Person.class));
	}

	@Test
	void testGetProperNoun() {
		final String expected = "John Doe";
		assertEquals(expected, Utils.getProperNoun(expected));
		assertEquals(expected, Utils.getProperNoun("john doe"));
		assertEquals(expected, Utils.getProperNoun("jOhn DoE"));
		assertEquals("John N'Aidoe", Utils.getProperNoun("jOhn n'aiDoE"));
		assertEquals("John-N'Ai'Doe", Utils.getProperNoun("jOhn-N'ai'DoE"));
		assertEquals("Jean*", Utils.getProperNoun("jEaN*"));
	}

	@Test
	void testGetProperNoun_WithBadInput() {
		assertThrows(NullPointerException.class, () -> Utils.getProperNoun(null));
	}

	@Test
	void testIndexOfNull() {
		Person[] persons = new Person[10];
		IntStream.range(0, 10).forEach(i -> {
			Person person = new Person();
			person.setEmail(RandomUtils.randomEmail());
			person.setName(String.format("person-%d", i + 1));
			persons[i] = person;
		});
		Predicate<Person> predicate = p -> {
			return p.getEmail() == null;
		};
		assertEquals(-1, Utils.indexOfNull(predicate, persons));
		persons[3].setEmail(null);
		assertEquals(3, Utils.indexOfNull(predicate, persons));
		persons[1] = null;
		assertEquals(1, Utils.indexOfNull(null, persons));
	}

	@Test
	void testIndexOfNull_WithEmptyArray() {
		assertThrows(NullPointerException.class, () -> Utils.indexOfNull(null, new Person[0]));
	}

	@Test
	void testListAnnotations() {
		assertTrue(Utils.listAnnotations(SimpleAnnotation.class, SimpleType.class, String.class).isEmpty());
		assertEquals(1, Utils.listAnnotations(SimpleAnnotation.class, SimpleType.class, SimpleType.class).size());
		assertEquals(2, Utils.listAnnotations(SimpleAnnotation.class, SimpleType.class, GenericType.class).size());
		assertEquals(0, Utils.listAnnotations(Email.class, SimpleType.class, GenericType.class).size());
	}

	@Test
	void testListCountriesAndCurrencies() {
		assertFalse(Utils.listCountries().isEmpty());
		assertFalse(Utils.listCurrencies().isEmpty());
	}

	@Test
	void testSafeLocale() {
		assertEquals(Locale.ENGLISH, Utils.safeLocale(Locale.ENGLISH.getLanguage()));
		assertEquals(Locale.FRENCH, Utils.safeLocale(Locale.FRENCH.getLanguage()));
		assertEquals(Utils.DEFAULT_LOCALE, Utils.safeLocale("unknown locale"));
		assertEquals(Utils.DEFAULT_LOCALE, Utils.safeLocale("fr_FR"));
	}

	@Test
	void testSetFieldValue() {
		final String languageField = "language";
		final String createdByField = "createdBy";
		final String language1 = "en";
		final String language2 = "fr";
		Person person = new Person();
		assertNull(person.getLanguage());
		Utils.setFieldValue(languageField, person, Person.class, language1);
		assertEquals(language1, person.getLanguage());
		Utils.setFieldValue(languageField, person, BaseEntity.class, language2);
		assertEquals(language2, person.getLanguage());
		Utils.setFieldValue(createdByField, person, BaseEntity.class, language2);
		assertEquals(language2, person.getCreatedBy());
	}

	@Test
	void testSetFieldValue_WithBadSupertype() {
		assertThrows(StandardRuntimeException.class,
				() -> Utils.setFieldValue("created", new Person(), Person.class, LocalDateTime.now()));
	}

	@Test
	void testSetFieldValue_WithBadValue() {
		assertThrows(StandardRuntimeException.class,
				() -> Utils.setFieldValue("updated", new Person(), BaseEntity.class, "Wrong value"));
	}
}
