package com.github.gcorporationcare.web.controller;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.annotation.PostConstruct;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;

import com.github.gcorporationcare.data.constraint.ValidationStep;
import com.github.gcorporationcare.data.entity.BaseEntity;
import com.github.gcorporationcare.web.domain.FieldFilter;
import com.github.gcorporationcare.web.dto.BaseDto;
import com.google.common.collect.Streams;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseController<D extends BaseDto, E extends BaseEntity> {

	Class<D> dtoClazz;
	Class<E> entityClazz;
	@Autowired
	protected ModelMapper modelMapper;

	@PostConstruct
	@SuppressWarnings("unchecked")
	public void init() {
		dtoClazz = (Class<D>) this.getGenericClass(0);
		entityClazz = (Class<E>) this.getGenericClass(1);
	}

	private Class<?> getGenericClass(int index) {
		Type type = getClass().getGenericSuperclass();
		if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			Type[] typeArguments = parameterizedType.getActualTypeArguments();
			return (Class<?>) typeArguments[index];
		}
		return null;
	}

	/**
	 * Conversion from entity to matching DTO
	 * 
	 * @param entity      the entity to convert
	 * @param fieldFilter used for hiding/displaying some fields
	 * @return the corresponding DTO
	 */
	public D mapToDto(@NonNull E entity, @NonNull FieldFilter<D> fieldFilter) {
		D dto = modelMapper.map(entity, dtoClazz);
		return fieldFilter.parseEntity(dto);
	}

	/**
	 * Convert from DTO to entity
	 * 
	 * @param dto the object to convert from
	 * @return the matching entity
	 */
	public E mapFromDto(@NonNull D dto) {
		return modelMapper.map(dto, entityClazz);
	}

	/**
	 * Conversion from list of entities to list of matching DTO
	 * 
	 * @param entities    the list of entities to convert
	 * @param fieldFilter used for hiding/displaying some fields
	 * @return the list of corresponding DTO
	 */
	public Iterable<D> mapToIterableDto(@NonNull Iterable<E> entities, @NonNull FieldFilter<D> fieldFilter) {
		return Streams.stream(entities).map(e -> mapToDto(e, fieldFilter)).collect(Collectors.toList());
	}

	/**
	 * Convert from list of DTO to list of entity
	 * 
	 * @param dtos the list of objects to convert from
	 * @return the matching list of entities
	 */
	public Iterable<E> mapFromIterableDto(@NonNull Iterable<D> dtos) {
		return StreamSupport.stream(dtos.spliterator(), false).map(d -> modelMapper.map(d, entityClazz))
				.collect(Collectors.toList());
	}

	/**
	 * Conversion from page of entities to page of matching DTO
	 * 
	 * @param page        the page of entities to convert
	 * @param fieldFilter used for hiding/displaying some fields
	 * @return the corresponding page of DTO
	 */
	public Page<D> mapToPageDto(@NonNull Page<E> page, @NonNull FieldFilter<D> fieldFilter) {
		List<D> dtos = (List<D>) mapToIterableDto(page.getContent(), fieldFilter);
		return new PageImpl<>(dtos, PageRequest.of(page.getPageable().getPageNumber(), page.getSize()),
				page.getTotalElements());
	}

	protected void validateCreateMultiple(@Validated({ ValidationStep.OnCreate.class }) D dto) {
		log.debug("Entity {} is valid", dto);
	}
}
