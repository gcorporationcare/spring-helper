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
import com.github.gcorporationcare.data.domain.SearchFilter.SearchFilterOperator;
import com.github.gcorporationcare.data.domain.SearchFilters;
import com.github.gcorporationcare.data.entity.BaseEntity;
import com.github.gcorporationcare.data.i18n.I18nMessage;
import com.github.gcorporationcare.data.repository.BaseRepository;
import com.github.gcorporationcare.web.exception.RequestException;
import com.google.common.collect.Streams;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional(propagation = Propagation.REQUIRED)
public abstract class BaseSlaveRegistrableService<E extends BaseEntity, ID extends Serializable, R extends BaseRepository<E, ID> & PagingAndSortingRepository<E, ID>, M_ID extends Serializable>
		extends BaseSlaveSearchableService<E, ID, R, M_ID> {

	public E getObject(ID id) {
		Optional<E> object = repository().findById(id);
		if (!object.isPresent())
			throw new RequestException(I18nMessage.RequestError.OBJECT_NOT_FOUND, HttpStatus.NOT_FOUND, getIdField(),
					id);
		return object.get();
	}

	public abstract void canCreate(@NonNull M_ID masterId, @NonNull E entity);

	protected void afterCreate(@NonNull E entity) {
		log.info("Created {}", entity);
	}

	@Transactional
	public E create(@NonNull M_ID masterId, @NonNull E entity) {
		Utils.setFieldValue(getMasterField(), entity, BaseEntity.class, masterId);
		canCreate(masterId, entity);
		E saved = repository().save(entity);
		afterCreate(saved);
		return saved;
	}

	@Transactional
	public Iterable<E> createMultiple(@NonNull M_ID masterId, @NonNull Iterable<E> entities) {
		Streams.stream(entities).forEach(e -> {
			Utils.setFieldValue(getMasterField(), e, BaseEntity.class, masterId);
			this.canCreate(masterId, e);
		});
		Iterable<E> saved = repository().saveAll(entities);
		StreamSupport.stream(saved.spliterator(), false).forEach(this::afterCreate);
		return saved;
	}

	public abstract void canUpdate(@NonNull M_ID masterId, @NonNull E entity, @NonNull E savedEntity);

	private E merge(@NonNull M_ID masterId, @NonNull ID id, @NonNull E entity, boolean excludeNull) {
		E saved = getObject(id);
		Utils.setFieldValue(getIdField(), entity, BaseEntity.class, id);
		Utils.setFieldValue(getMasterField(), entity, BaseEntity.class, masterId);
		String[] excludedFields = excludeNull ? Utils.getNullPropertyNames(entity) : null;
		canUpdate(masterId, entity, saved);
		saved.merge(entity, excludedFields);
		return repository().save(saved);
	}

	protected void afterUpdate(E entity) {
		log.info("Updated {}", entity);
	}

	@Transactional
	public E update(@NonNull M_ID masterId, @NonNull ID id, @NonNull E entity) {
		E saved = merge(masterId, id, entity, false);
		afterUpdate(saved);
		return saved;
	}

	@Transactional
	public E patch(@NonNull M_ID masterId, @NonNull ID id, @NonNull E entity) {
		E saved = merge(masterId, id, entity, true);
		afterUpdate(saved);
		return saved;
	}

	public abstract void canDelete(@NonNull M_ID masterId, @NonNull E entity);

	@Transactional
	public void delete(@NonNull M_ID masterId, @NonNull ID id) {
		// Since we will need all available fields to decide whether an user can delete
		// a record or not
		E entity = read(masterId, id);
		canDelete(masterId, entity);
		repository().deleteById(id);
	}

	@Transactional
	public void deleteMultiple(@NonNull M_ID masterId, @NonNull Iterable<ID> ids) {
		SearchFilters<E> idsFilters = new SearchFilters<>();
		Streams.stream(ids).filter(Objects::nonNull)
				.forEach(i -> idsFilters.or(getIdField(), SearchFilterOperator.IS_EQUAL, i));
		List<E> entities = readMultiple(masterId, idsFilters, null).getContent();
		entities.stream().forEach(e -> this.canDelete(masterId, e));
		repository().deleteAll();
	}
}
