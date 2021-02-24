package com.gcorp.common;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.metadata.ConstraintDescriptor;

import org.hibernate.annotations.common.annotationfactory.AnnotationDescriptor;
import org.hibernate.annotations.common.annotationfactory.AnnotationFactory;
import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;
import org.hibernate.validator.internal.util.annotation.ConstraintAnnotationDescriptor;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.ReflectionUtils;

import com.gcorp.entity.BaseEntity;
import com.gcorp.exception.StandardRuntimeException;
import com.gcorp.field.Country;
import com.gcorp.field.MoneyCurrency;
import com.google.common.base.Strings;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Utils {

	public static final String API_DATE_FORMAT = "yyyy-MM-dd";
	public static final String API_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'.'SSSSZ";
	public static final String API_TIME_FORMAT = "HH:mm:ss'.'SSSS";
	private static List<Country> countries;
	private static List<MoneyCurrency> currencies;

	public static final Locale DEFAULT_LOCALE = Locale.ENGLISH;
	public static final String UTC_ZONE = "UTC";

	/**
	 * Generate javax.validation violations messages
	 * 
	 * @param <T>     The bean type
	 * @param <U>     The constraint type
	 * @param clazz   The annotation constraint class
	 * @param message the message that will be displayed
	 * @param bean    the object having the bad field
	 * @param field   the field with the non acceptable value
	 * @param value   the given value
	 * @return a set of constraint violation message
	 */
	public static <T, U extends Annotation> Set<ConstraintViolation<Object>> generateViolations(final Class<U> clazz,
			final String message, final T bean, final String field, final Object value) {
		Set<ConstraintViolation<Object>> violations = new HashSet<>();
		final String messageTemplate = String.format("{%s}", message);
		final Path path = PathImpl.createPathFromString(field);
		final ConstraintHelper helper = new ConstraintHelper();
		AnnotationDescriptor descriptor = new AnnotationDescriptor(clazz);
		descriptor.setValue("message", message);
		descriptor.setValue("value", value != null ? new String[] { value.toString() } : new String[] { "" });
		Field member = getInheritedField(field, bean.getClass(), BaseEntity.class);
		U annotation = AnnotationFactory.create(descriptor);
		ConstraintAnnotationDescriptor<U> constraintAnnotationDescriptor = new ConstraintAnnotationDescriptor<>(
				annotation);
		final ConstraintDescriptor<U> constraintDescriptor = new ConstraintDescriptorImpl<>(helper, member,
				constraintAnnotationDescriptor, ElementType.FIELD);
		final Map<String, Object> messageParameters = new HashMap<>();
		final Map<String, Object> expressionVariables = new HashMap<>();
		violations.add(ConstraintViolationImpl.forBeanValidation(messageTemplate, messageParameters,
				expressionVariables, message, Object.class, bean, new Object(), value, path, constraintDescriptor,
				ElementType.FIELD, null));
		return violations;
	}

	/**
	 * Get the type of a parameterized class
	 * 
	 * @param genericClass a generic class
	 * @return the type of the first generic parameter
	 */
	public static Type getParameterizedType(Class<?> genericClass) {
		if (genericClass == null)
			throw new NullPointerException("Cannot get class from null generic class");
		Type type = genericClass.getGenericSuperclass();
		if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			Type[] typeArguments = parameterizedType.getActualTypeArguments();
			return typeArguments[0];
		}
		throw new IllegalArgumentException("Given super class hasn't any generic parameter");
	}

	/**
	 * Get list of properties which are null
	 * 
	 * @param source any object with eventually null properties
	 * @return the list of properties having null values
	 */
	public static String[] getNullPropertyNames(Object source) {
		final BeanWrapper src = new BeanWrapperImpl(source);
		java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

		Set<String> emptyNames = new HashSet<>();
		for (java.beans.PropertyDescriptor pd : pds) {
			Object srcValue = src.getPropertyValue(pd.getName());
			if (srcValue == null)
				emptyNames.add(pd.getName());
		}
		String[] result = new String[emptyNames.size()];
		return emptyNames.toArray(result);
	}

	/**
	 * Get the principal of current authenticated user
	 * 
	 * @return the UserDetails of the current authenticated user (null if none)
	 */
	public static UserDetails getAuthenticatedUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()
				|| !UserDetails.class.isAssignableFrom(authentication.getPrincipal().getClass())) {
			return null;
		}
		return (UserDetails) authentication.getPrincipal();
	}

	/**
	 * Get (via reflection) the current value of a given field on an object
	 * 
	 * @param <T>       The object's type
	 * @param fieldName the name of the field
	 * @param object    the object owning the field
	 * @param superType in case the field is in a super class of given object, will
	 *                  loop from given class to super class
	 * @return the field's value
	 */
	public static <T> Object getFieldValue(String fieldName, T object, Class<?> superType) {
		try {
			Field field = getInheritedField(fieldName, object.getClass(), superType);
			ReflectionUtils.makeAccessible(field);
			return ReflectionUtils.getField(field, object);
		} catch (SecurityException | IllegalArgumentException e) {
			throw new StandardRuntimeException(e);
		}
	}

	/**
	 * Get declared fields for classes and super-classes until expected level
	 * 
	 * @param fieldName the field we are looking for
	 * @param type      class to start the collecting from
	 * @param superType parent class of former argument where to stop the collecting
	 * @return the list of fields
	 */
	public static Field getInheritedField(String fieldName, Class<?> type, Class<?> superType) {
		Optional<Field> field = Utils.getInheritedFields(type, superType).stream()
				.filter(f -> fieldName.equals(f.getName())).findFirst();
		if (!field.isPresent())
			throw new StandardRuntimeException(String.format("Field %s not found", fieldName));
		return field.get();
	}

	/**
	 * Get declared fields for classes and super-classes until expected level
	 * 
	 * @param type      class to start the collecting from
	 * @param superType parent class of former argument where to stop the collecting
	 * @return the list of fields
	 */
	public static List<Field> getInheritedFields(Class<?> type, Class<?> superType) {
		List<Field> fields = new ArrayList<>();
		if (!superType.isAssignableFrom(type))
			return fields;
		for (Class<?> clazz = type; clazz != superType; clazz = clazz.getSuperclass()) {
			fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
		}
		fields.addAll(Arrays.asList(superType.getDeclaredFields()));
		return fields;
	}

	/**
	 * Validate a String by testing if it is null or empty<br>
	 * Also clean the string from blank space and allow it to start by a camel case
	 * while following with lower cases
	 * 
	 * @param string The string to check
	 * @return the cleaned string
	 */
	public static String getProperNoun(String string) {
		if (Strings.isNullOrEmpty(string))
			throw new NullPointerException("Cannot format null string");
		Pattern special = Pattern.compile("\\W");
		String[] letters = string.toLowerCase().split("");
		StringBuilder sb = new StringBuilder();
		// First viable later must be capitalized
		boolean capitalize = true;
		for (String letter : letters) {
			Matcher matcher = special.matcher(letter);
			boolean isSpecial = matcher.matches();
			if (isSpecial || !capitalize) {
				// We do nothing if special character
				sb.append(letter);
				capitalize = capitalize || isSpecial;
				continue;
			}
			sb.append(letter.toUpperCase());
			capitalize = false;
		}
		return sb.toString();
	}

	/**
	 * Get the user's name of current authenticated user
	 * 
	 * @return a String representing the name of the current authenticated user or
	 *         N/A if none
	 */
	public static String getUsernameOfAuthenticatedUser() {
		UserDetails securedUser = getAuthenticatedUser();
		return (securedUser != null) ? securedUser.getUsername() : "N/A";
	}

	/**
	 * Get the index of the first non null value
	 * 
	 * @param <T>       any type
	 * @param predicate method allowing to verify if items are null
	 * @param items     the array of objects to check
	 * @return -1 when none found
	 */
	@SafeVarargs
	public static <T> int indexOfNull(Predicate<T> predicate, T... items) {
		if (items == null || items.length == 0)
			throw new NullPointerException("Expecting an non null and non empty array");
		for (int index = 0; index < items.length; index++) {
			T item = items[index];
			if (item == null || predicate != null && predicate.test(item))
				return index;
		}
		return -1;
	}

	/**
	 * List annotation in classes between given class and its parent
	 * 
	 * @param <T>             a annotation type
	 * @param annotationClass the annotation we are looking for
	 * @param type            the starting class
	 * @param superType       the stopping class (must be superclass of previous)
	 * @return the list of annotation find between the start and stop
	 */
	public static <T extends Annotation> List<T> listAnnotations(Class<T> annotationClass, Class<?> type,
			Class<?> superType) {
		if (!superType.isAssignableFrom(type))
			return new ArrayList<>();
		List<T> annotations = new ArrayList<>();
		for (Class<?> clazz = type; superType.isAssignableFrom(clazz); clazz = clazz.getSuperclass()) {
			if (!clazz.isAnnotationPresent(annotationClass))
				continue;
			annotations.add(clazz.getAnnotation(annotationClass));
		}
		return annotations;
	}

	/**
	 * Get the list of countries
	 * 
	 * @return list containing known countries
	 */
	public static List<Country> listCountries() {
		if (countries == null) {
			countries = Country.listCountries(null);
		}
		return countries;
	}

	/**
	 * Get the list of currencies
	 * 
	 * @return list containing known currencies
	 */
	public static List<MoneyCurrency> listCurrencies() {
		if (currencies == null) {
			currencies = MoneyCurrency.listCurrencies(null);
		}
		return currencies;
	}

	/**
	 * Get a Locale from a given String If isn't a valid locale code, will return a
	 * default locale
	 * 
	 * @param language the locale code
	 * @return the Locale to use
	 */
	public static Locale safeLocale(String language) {
		if (Strings.isNullOrEmpty(language)) {
			return DEFAULT_LOCALE;
		}
		try {
			Locale locale = new Locale(language.trim());
			return locale.getISO3Language() != null ? locale : DEFAULT_LOCALE;
		} catch (MissingResourceException e) {
			return DEFAULT_LOCALE;
		}
	}

	/**
	 * Set (via reflection) a value on a given field on an object
	 * 
	 * @param <T>       The object's type
	 * @param fieldName the name of the field
	 * @param object    the object owning the field
	 * @param superType in case the field is in a super class of given object, will
	 *                  loop from given class to super class
	 * @param value     the value to set
	 */
	public static <T> void setFieldValue(String fieldName, T object, Class<?> superType, Object value) {
		try {
			Field field = getInheritedField(fieldName, object.getClass(), superType);
			ReflectionUtils.makeAccessible(field);
			ReflectionUtils.setField(field, object, value);
		} catch (SecurityException | IllegalArgumentException e) {
			throw new StandardRuntimeException(e);
		}
	}
}
