package com.github.gcorporationcare.web.service;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.gcorporationcare.data.common.Utils;
import com.github.gcorporationcare.data.domain.FieldFilter;
import com.github.gcorporationcare.data.domain.SearchFilters;
import com.github.gcorporationcare.data.domain.SearchFilter.SearchFilterOperator;
import com.github.gcorporationcare.data.entity.BaseEntity;
import com.github.gcorporationcare.data.i18n.I18nMessage;
import com.github.gcorporationcare.data.repository.BaseRepository;
import com.github.gcorporationcare.web.exception.RequestException;
import com.google.common.collect.Streams;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional(propagation = Propagation.REQUIRED)
public abstract class BaseRegistrableService<E extends BaseEntity, ID extends Serializable, R extends BaseRepository<E, ID> & PagingAndSortingRepository<E, ID>>
		extends BaseSearchableService<E, ID, R> {

	public E getObject(ID id) {
		Optional<E> object = repository().findById(id);
		if (!object.isPresent())
			throw new RequestException(I18nMessage.RequestError.OBJECT_NOT_FOUND, HttpStatus.NOT_FOUND, getIdField(),
					id);
		return object.get();
	}

	public abstract void canCreate(@NonNull E entity);

	protected void afterCreate(E entity) {
		log.info("Created {}", entity);
	}

	@Transactional
	public E create(E entity, @NonNull FieldFilter<E> fieldFilter) {
		canCreate(entity);
		E saved = repository().save(entity);
		afterCreate(saved);
		return fieldFilter.parseEntity(saved);
	}

	@Transactional
	public Iterable<E> createMultiple(@NonNull Iterable<E> entities, @NonNull FieldFilter<E> fieldFilter) {
		Streams.stream(entities).forEach(this::canCreate);
		Iterable<E> saved = repository().saveAll(entities);
		StreamSupport.stream(saved.spliterator(), false).forEach(this::afterCreate);
		return fieldFilter.parseIterable(saved);
	}

	public abstract void canUpdate(@NonNull E entity, @NonNull E savedEntity);

	private E merge(@NonNull ID id, @NonNull E entity, boolean excludeNull) {
		E saved = getObject(id);
		Utils.setFieldValue(getIdField(), entity, BaseEntity.class, id);
		String[] excludedFields = excludeNull ? Utils.getNullPropertyNames(entity) : null;
		canUpdate(entity, saved);
		saved.merge(entity, excludedFields);
		return repository().save(saved);
	}

	protected void afterUpdate(E entity) {
		log.info("Updated {}", entity);
	}

	@Transactional
	public E update(@NonNull ID id, @NonNull E entity, @NonNull FieldFilter<E> fieldFilter) {
		E saved = merge(id, entity, false);
		afterUpdate(saved);
		return fieldFilter.parseEntity(saved);
	}

	@Transactional
	public E patch(@NonNull ID id, @NonNull E entity, @NonNull FieldFilter<E> fieldFilter) {
		E saved = merge(id, entity, true);
		afterUpdate(saved);
		return fieldFilter.parseEntity(saved);
	}

	public abstract void canDelete(@NonNull E entity);

	@Transactional
	public void delete(@NonNull ID id) {
		// Since we will need all available fields to decide whether an user can delete
		// a record or not
		E entity = read(id, FieldFilter.allFields());
		canDelete(entity);
		repository().deleteById(id);
	}

	@Transactional
	public void deleteMultiple(@NonNull Iterable<ID> ids) {
		SearchFilters<E> idsFilters = new SearchFilters<>();
		Streams.stream(ids).filter(Objects::nonNull)
				.forEach(i -> idsFilters.or(getIdField(), SearchFilterOperator.IS_EQUAL, i));
		List<E> entities = readMultiple(idsFilters, FieldFilter.allFields(), null).getContent();
		entities.stream().forEach(this::canDelete);
		repository().deleteAll();
	}
}