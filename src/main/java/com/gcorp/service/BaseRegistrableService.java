package com.gcorp.service;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.gcorp.common.Utils;
import com.gcorp.domain.FieldFilter;
import com.gcorp.domain.SearchFilter.SearchFilterOperator;
import com.gcorp.domain.SearchFilters;
import com.gcorp.entity.BaseEntity;
import com.gcorp.exception.RequestException;
import com.gcorp.i18n.I18nMessage;
import com.gcorp.repository.BaseRepository;
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
		Streams.stream(ids).filter(i -> i != null)
				.forEach(i -> idsFilters.or(getIdField(), SearchFilterOperator.IS_EQUAL, i));
		List<E> entities = repository().findByFilters(idsFilters, null).getContent();
		entities.stream().forEach(this::canDelete);
		repository().deleteAll();
	}
}
