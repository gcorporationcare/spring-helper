package com.gcorp.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import javax.validation.ConstraintViolation;
import javax.validation.constraints.Email;

import org.apache.commons.lang3.ArrayUtils;
import org.joda.time.LocalDateTime;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.gcorp.constraint.InvalidWhen;
import com.gcorp.entity.BaseEntity;
import com.gcorp.exception.StandardRuntimeException;
import com.gcorp.notest.Person;
import com.gcorp.notest.RandomUtils;

import lombok.AllArgsConstructor;

public class UtilsTest {
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

	class AccessRight implements GrantedAuthority {
		private static final long serialVersionUID = 1L;

		@Override
		public String getAuthority() {
			return "test";
		}
	}

	@AllArgsConstructor
	class User implements UserDetails {
		private static final long serialVersionUID = 1L;
		String name;
		List<AccessRight> rights;

		@Override
		public Collection<? extends GrantedAuthority> getAuthorities() {
			return rights;
		}

		@Override
		public String getPassword() {
			return name;
		}

		@Override
		public String getUsername() {
			return name;
		}

		@Override
		public boolean isAccountNonExpired() {
			return true;
		}

		@Override
		public boolean isAccountNonLocked() {
			return true;
		}

		@Override
		public boolean isCredentialsNonExpired() {
			return true;
		}

		@Override
		public boolean isEnabled() {
			return true;
		}
	}

	@Test
	public void testGenerateViolations() {
		final String emailField = "email";
		final String message = "Email not valid";
		Person person = new Person();
		person.setEmail("alpha.com");
		Set<ConstraintViolation<Object>> violations = Utils.generateViolations(InvalidWhen.class, message, person,
				emailField, person.getEmail());
		Assert.assertEquals(1, violations.size());
		ConstraintViolation<Object> violation = violations.stream().findFirst().get();
		Assert.assertEquals(person.getEmail(), violation.getInvalidValue());
		Assert.assertEquals(message, violation.getMessage());
		Assert.assertEquals(emailField, violation.getPropertyPath().toString());

		Assert.assertFalse(Utils.generateViolations(InvalidWhen.class, message, person, emailField, null).isEmpty());
	}

	@Test
	public void testGetParameterizedType() {
		Assert.assertEquals(String.class, Utils.getParameterizedType(SimpleType.class));
	}

	@Test(expected = NullPointerException.class)
	public void testGetParameterizedType_WithNoArgument() {
		Utils.getParameterizedType(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetParameterizedType_WithNoGenericClass() {
		Utils.getParameterizedType(String.class);
	}

	@Test
	public void testGetNullPropertyNames() {
		Person person = new Person();
		String[] nullProperties = Utils.getNullPropertyNames(person);
		Assert.assertNotNull(nullProperties);
		Assert.assertTrue(ArrayUtils.contains(nullProperties, "name"));
		person.setName("A given name");
		Assert.assertEquals(nullProperties.length - 1, Utils.getNullPropertyNames(person).length);
	}

	@Test
	public void testGetAuthenticatedUser() {
		Assert.assertNull(Utils.getAuthenticatedUser());
		final String username = "a given user";
		User user = new User(username, Arrays.asList(new AccessRight[] { new AccessRight() }));
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(
				new UsernamePasswordAuthenticationToken(user, user.getUsername(), user.getAuthorities()));
		SecurityContextHolder.setContext(context);
		Assert.assertEquals(username, Utils.getAuthenticatedUser().getUsername());
		context.setAuthentication(new UsernamePasswordAuthenticationToken(user, user.getUsername()));
		Assert.assertNull(Utils.getAuthenticatedUser());
		context.setAuthentication(
				new UsernamePasswordAuthenticationToken(username, user.getUsername(), user.getAuthorities()));
		Assert.assertNull(Utils.getAuthenticatedUser());
	}

	@Test
	public void testGetFieldValue() {
		final String nameField = "name";
		Person person = new Person();
		person.setName("A given name");
		Assert.assertEquals(person.getName(), Utils.getFieldValue(nameField, person, Person.class));
		Assert.assertEquals(person.getName(), Utils.getFieldValue(nameField, person, BaseEntity.class));
	}

	@Test(expected = StandardRuntimeException.class)
	public void testGetFieldValue_WithBadClass() {
		Utils.getFieldValue("unknown field", "Alphabet", Person.class);
	}

	@Test(expected = StandardRuntimeException.class)
	public void testGetFieldValue_WithUnknownField() {
		Person person = new Person();
		Assert.assertEquals(person.getName(), Utils.getFieldValue("weird field", person, Person.class));
	}

	@Test
	public void testGetInheritedField() {
		Assert.assertNotNull(Utils.getInheritedField("created", Person.class, BaseEntity.class));
	}

	@Test
	public void testGetInheritedFields() {
		Assert.assertFalse(Utils.getInheritedFields(Person.class, BaseEntity.class).isEmpty());
	}

	@Test
	public void testGetInheritedFields_WithBadSuperType() {
		Assert.assertTrue(Utils.getInheritedFields(Person.class, String.class).isEmpty());
	}

	@Test(expected = StandardRuntimeException.class)
	public void testGetFieldValue_WithNotDeepSuperClass() {
		Person person = new Person();
		Assert.assertEquals(person.getCreatedBy(), Utils.getFieldValue("createdBy", person, Person.class));
	}

	@Test
	public void testGetProperNoun() {
		final String expected = "John Doe";
		Assert.assertEquals(expected, Utils.getProperNoun(expected));
		Assert.assertEquals(expected, Utils.getProperNoun("john doe"));
		Assert.assertEquals(expected, Utils.getProperNoun("jOhn DoE"));
		Assert.assertEquals("John N'Aidoe", Utils.getProperNoun("jOhn n'aiDoE"));
		Assert.assertEquals("John-N'Ai'Doe", Utils.getProperNoun("jOhn-N'ai'DoE"));
		Assert.assertEquals("Jean*", Utils.getProperNoun("jEaN*"));
	}

	@Test(expected = NullPointerException.class)
	public void testGetProperNoun_WithBadInput() {
		Utils.getProperNoun(null);
	}

	@Test
	public void testGetUsernameOfAuthenticatedUser() {
		Assert.assertNotNull(Utils.getUsernameOfAuthenticatedUser());
		final String username = "another user";
		User user = new User(username, Arrays.asList(new AccessRight[] { new AccessRight() }));
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(
				new UsernamePasswordAuthenticationToken(user, user.getUsername(), user.getAuthorities()));
		SecurityContextHolder.setContext(context);
		Assert.assertEquals(username, Utils.getUsernameOfAuthenticatedUser());
	}

	@Test
	public void testIndexOfNull() {
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
		Assert.assertEquals(-1, Utils.indexOfNull(predicate, persons));
		persons[3].setEmail(null);
		Assert.assertEquals(3, Utils.indexOfNull(predicate, persons));
		persons[1] = null;
		Assert.assertEquals(1, Utils.indexOfNull(null, persons));
	}

	@Test(expected = NullPointerException.class)
	public void testIndexOfNull_WithEmptyArray() {
		Utils.indexOfNull(null, new Person[0]);
	}

	@Test
	public void testListAnnotations() {
		Assert.assertTrue(Utils.listAnnotations(SimpleAnnotation.class, SimpleType.class, String.class).isEmpty());
		Assert.assertEquals(1,
				Utils.listAnnotations(SimpleAnnotation.class, SimpleType.class, SimpleType.class).size());
		Assert.assertEquals(2,
				Utils.listAnnotations(SimpleAnnotation.class, SimpleType.class, GenericType.class).size());
		Assert.assertEquals(0, Utils.listAnnotations(Email.class, SimpleType.class, GenericType.class).size());
	}

	@Test
	public void testListCountriesAndCurrencies() {
		Assert.assertFalse(Utils.listCountries().isEmpty());
		Assert.assertFalse(Utils.listCurrencies().isEmpty());
	}

	@Test
	public void testSafeLocale() {
		Assert.assertEquals(Locale.ENGLISH, Utils.safeLocale(Locale.ENGLISH.getLanguage()));
		Assert.assertEquals(Locale.FRENCH, Utils.safeLocale(Locale.FRENCH.getLanguage()));
		Assert.assertEquals(Utils.DEFAULT_LOCALE, Utils.safeLocale("unknown locale"));
		Assert.assertEquals(Utils.DEFAULT_LOCALE, Utils.safeLocale("fr_FR"));
	}

	@Test
	public void testSetFieldValue() {
		final String languageField = "language";
		final String createdByField = "createdBy";
		final String language1 = "en";
		final String language2 = "fr";
		Person person = new Person();
		Assert.assertNull(person.getLanguage());
		Utils.setFieldValue(languageField, person, Person.class, language1);
		Assert.assertEquals(language1, person.getLanguage());
		Utils.setFieldValue(languageField, person, BaseEntity.class, language2);
		Assert.assertEquals(language2, person.getLanguage());
		Utils.setFieldValue(createdByField, person, BaseEntity.class, language2);
		Assert.assertEquals(language2, person.getCreatedBy());
	}

	@Test(expected = StandardRuntimeException.class)
	public void testSetFieldValue_WithBadSupertype() {
		Utils.setFieldValue("created", new Person(), Person.class, LocalDateTime.now());
	}

	@Test(expected = StandardRuntimeException.class)
	public void testSetFieldValue_WithBadValue() {
		Utils.setFieldValue("updated", new Person(), BaseEntity.class, "Wrong value");
	}
}
