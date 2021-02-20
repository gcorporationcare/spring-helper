package com.gcorp.domain;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gcorp.serializer.LocalDateTimeDeserializer;
import com.gcorp.serializer.LocalDateTimeSerializer;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FieldFilter<T extends FieldFilterable> implements Serializable {

	public static final String JSON_FILTER_NAME = "fields";
	public static final String ALL_FIELDS = "_all_";
	/**
	 * If sent string starts by this sign, all the listed fields will be added to
	 * defaults
	 */
	public static final String ADD_TO_DEFAULTS = "+";
	/**
	 * If sent string starts by this sign, all the listed fields will be removed
	 * from all fields
	 */
	public static final String REMOVE_FROM_ALL = "-";
	/**
	 * Delimiting different fields' names
	 */
	public static final String FIELDS_DELIMITER = ",";

	private static final long serialVersionUID = 1L;

	/**
	 * If null, only the listed fields will be displayed
	 */
	private String sign;
	private String[] fields;

	public void setToAllIfEmpty() {
		if (fields != null && fields.length > 0) {
			return;
		}
		fields = new String[] { ALL_FIELDS };
	}

	public boolean isAll() {
		return fields != null && Arrays.asList(fields).contains(ALL_FIELDS);
	}

	public boolean isDefault() {
		return fields == null || ArrayUtils.isEmpty(fields);
	}

	public boolean isRemoveFromAll() {
		boolean hasFields = !isDefault();
		return hasFields && !Strings.isNullOrEmpty(sign) && REMOVE_FROM_ALL.equals(sign);
	}

	public boolean isAddToDefault() {
		return !Strings.isNullOrEmpty(sign) && ADD_TO_DEFAULTS.equals(sign);
	}

	public Set<String> activeFields(T entity) {
		Set<String> minimalFields = minimalFields();
		Set<String> defaultFields = entity.defaultFields();
		defaultFields.addAll(minimalFields);
		// 2- Must return only default fields
		if (this.isDefault()) {
			return defaultFields;
		}
		// 3- Must add some fields to default
		if (this.isAddToDefault()) {
			defaultFields.addAll(new HashSet<>(Arrays.asList(fields)));
			return defaultFields;
		}
		// 4- Must return exactly what is requested
		minimalFields.addAll(new HashSet<>(Arrays.asList(fields)));
		return minimalFields;
	}

	@SuppressWarnings("unchecked")
	public Iterable<T> parseIterable(@NonNull Iterable<T> list) {
		if (this.isAll() || ((Collection<?>) list).isEmpty()) {
			// Nothing to do here
			return list;
		}
		ObjectMapper mapper = this.objectMapper(Iterables.get(list, 0));
		try {
			return mapper.readValue(mapper.writeValueAsString(list), ArrayList.class);
		} catch (IOException e) {
			log.error("An error occured during serialization of list {}", list);
			log.error("Error description", e);
		}
		return list;
	}

	public Page<T> parsePage(@NonNull Page<T> page) {
		if (this.isAll()) {
			// Nothing to do here
			return page;
		}
		List<T> content = (List<T>) parseIterable(page.getContent());
		return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
	}

	public T parseEntity(@NonNull T entity) {
		if (this.isAll()) {
			// Nothing to do here
			return entity;
		}
		ObjectMapper mapper = this.objectMapper(entity);
		try {
			final String json = mapper.writeValueAsString(entity);
			ObjectMapper defaultMapper = defaultObjectMapper();
			defaultMapper.configure(MapperFeature.USE_ANNOTATIONS, false);
			defaultMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			@SuppressWarnings("unchecked")
			T parsed = (T) defaultMapper.readValue(json, entity.getClass());
			return parsed;
		} catch (IOException e) {
			log.error("An error occured during serialization of entity {}", entity);
			log.error("Error description", e);
		}
		return entity;
	}

	public static <T extends FieldFilterable> FieldFilter<T> allFields() {
		return FieldFilter.fromString(ALL_FIELDS);
	}

	public static <T extends FieldFilterable> FieldFilter<T> defaultFields() {
		return FieldFilter.fromString(null);
	}

	public static <T extends FieldFilterable> FieldFilter<T> fromString(String string) {
		FieldFilter<T> filter = new FieldFilter<>();
		if (Strings.isNullOrEmpty(string)) {
			// Active fields method will return defaults fields
			return filter;
		}
		String stringContent = detectSign(filter, string);
		String[] fields = stringContent.split(FIELDS_DELIMITER);
		filter.fields = Arrays.stream(fields).filter(f -> !Strings.isNullOrEmpty(f)).toArray(String[]::new);
		return filter;
	}

	private ObjectMapper objectMapper(T entity) {
		SimpleFilterProvider filterProvider = new SimpleFilterProvider();
		if (this.isRemoveFromAll()) {
			filterProvider.addFilter(JSON_FILTER_NAME,
					SimpleBeanPropertyFilter.serializeAllExcept(new HashSet<>(Arrays.asList(fields))));
		} else {
			filterProvider.addFilter(JSON_FILTER_NAME,
					SimpleBeanPropertyFilter.filterOutAllExcept(activeFields(entity)));
		}
		ObjectMapper mapper = defaultObjectMapper().setFilterProvider(filterProvider);
		mapper.configure(MapperFeature.USE_ANNOTATIONS, true);
		return mapper;
	}

	private Set<String> minimalFields() {
		// Fields that will always be included no matter what
		return new HashSet<>(Arrays.asList("id", "email", "code", "name"));
	}

	private static <T extends FieldFilterable> String detectSign(FieldFilter<T> filter, String string) {
		String stringContent = string;
		if (stringContent.startsWith(ADD_TO_DEFAULTS)) {
			filter.sign = ADD_TO_DEFAULTS;
			// Remove sign from content
			stringContent = stringContent.substring(ADD_TO_DEFAULTS.length());
		}
		if (stringContent.startsWith(REMOVE_FROM_ALL)) {
			filter.sign = REMOVE_FROM_ALL;
			// Remove sign from content
			stringContent = stringContent.substring(REMOVE_FROM_ALL.length());
		}
		return stringContent;
	}

	private static ObjectMapper defaultObjectMapper() {
		JavaTimeModule module = new JavaTimeModule();
		module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
		module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());

		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(module);
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		return mapper;
	}
}
