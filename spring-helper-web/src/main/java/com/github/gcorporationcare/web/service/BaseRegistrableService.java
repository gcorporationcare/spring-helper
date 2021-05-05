package com.github.gcorporationcare.web.service;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
public abstract class BaseRegistrableService<E extends BaseEntity, I extends Serializable, R extends BaseRepository<E, I> & PagingAndSortingRepository<E, I>>
		extends BaseSearchableService<E, I, R> {

	/**
	 * Used to check if user have required access to create data from this service.
	 * 
	 * @param authentication the current authentication object from security context
	 * @param entity         the record to save
	 * 
	 * @return true if user is allowed
	 */
	public boolean canCreate(Authentication authentication, E entity) {
		return true;
	}

	/**
	 * Used to check if user have required access to create data from this service.
	 * 
	 * @param authentication the current authentication object from security context
	 * @param entities       the records to save
	 * 
	 * @return true if user is allowed
	 */
	public final boolean canCreate(Authentication authentication, Iterable<E> entities) {
		return Streams.stream(entities).filter(e -> !canCreate(authentication, e)).count() == 0;
	}

	/**
	 * Used to check if user have required access to update data from this service.
	 * 
	 * @param authentication the current authentication object from security context
	 * @param id             the record ID
	 * @param entity         the record to save
	 * 
	 * @return true if user is allowed
	 */
	public boolean canUpdate(Authentication authentication, I id, E entity) {
		return true;
	}

	/**
	 * Used to check if user have required access to delete data from this service.
	 * 
	 * @param authentication the current authentication object from security context
	 * @param id             the record ID
	 * 
	 * @return true if user is allowed
	 */
	public boolean canDelete(Authentication authentication, I id) {
		return true;
	}

	/**
	 * Used to check if user have required access to delete data from this service.
	 * 
	 * @param authentication the current authentication object from security context
	 * @param ids            the record IDs
	 * 
	 * @return true if user is allowed
	 */
	public final boolean canDelete(Authentication authentication, Iterable<I> ids) {
		return Streams.stream(ids).filter(id -> !canDelete(authentication, id)).count() == 0;
	}

	public E getObject(I id) {
		Optional<E> object = repository().findById(id);
		if (!object.isPresent())
			throw new RequestException(I18nMessage.RequestError.OBJECT_NOT_FOUND, HttpStatus.NOT_FOUND, getIdField(),
					id);
		return object.get();
	}

	/**
	 * Do some action before creating
	 * 
	 * @param entity the entity to save
	 * @throws RequestException when something is off with given data
	 */
	protected void beforeCreate(@NonNull E entity) throws RequestException {
		log.debug("Attempting to create {}", entity);
	}

	/**
	 * Do some action after data has been created
	 * 
	 * @param entity the created data
	 */
	protected void afterCreate(@NonNull E entity) {
		log.debug("Created {}", entity);
	}

	@Transactional
	@PreAuthorize("this.canCreate(authentication, #entity)")
	public E create(@NonNull E entity) {
		beforeCreate(entity);
		E saved = repository().save(entity);
		afterCreate(saved);
		return saved;
	}

	@Transactional
	@PreAuthorize("this.canCreate(authentication, #entities)")
	public Iterable<E> createMultiple(@NonNull Iterable<E> entities) {
		Streams.stream(entities).forEach(this::beforeCreate);
		Iterable<E> saved = repository().saveAll(entities);
		Streams.stream(saved).forEach(this::afterCreate);
		return saved;
	}

	/**
	 * Do some action before updating
	 * 
	 * @param entity the entity to save
	 * @throws RequestException when something is off with given data
	 */
	protected void beforeUpdate(@NonNull E entity, @NonNull E savedEntity, boolean patching) {
		log.debug("Attempting to update {}", entity);
	}

	/**
	 * Do some action after data has been updated
	 * 
	 * @param entity the updated data
	 */
	protected void afterUpdate(@NonNull E entity) {
		log.debug("Updated {}", entity);
	}

	private E merge(@NonNull I id, @NonNull E entity, boolean patching) {
		E saved = getObject(id);
		Utils.setFieldValue(getIdField(), entity, BaseEntity.class, id);
		String[] excludedFields = patching ? Utils.getNullPropertyNames(entity) : null;
		beforeUpdate(entity, saved, patching);
		saved.merge(entity, excludedFields);
		saved = repository().save(saved);
		afterUpdate(saved);
		return saved;
	}

	@Transactional
	@PreAuthorize("this.canUpdate(authentication, #id, #entity)")
	public E update(@NonNull I id, @NonNull E entity) {
		return merge(id, entity, false);
	}

	@Transactional
	@PreAuthorize("this.canUpdate(authentication, #id, #entity)")
	public E patch(@NonNull I id, @NonNull E entity) {
		return merge(id, entity, true);
	}

	/**
	 * Do some action before deleting
	 * 
	 * @param entity the entity to remove
	 * @throws RequestException when something is off with given data
	 */
	protected void beforeDelete(@NonNull E entity) {
		log.debug("Attempting to delete {}", entity);
	}

	/**
	 * Do some action after data has been deleted
	 * 
	 * @param entity the removed data
	 */
	protected void afterDelete(@NonNull E entity) {
		log.debug("Deleted {}", entity);
	}

	@Transactional
	@PreAuthorize("this.canDelete(authentication, #id)")
	public void delete(@NonNull I id) {
		// Since we will need all available fields to decide whether an user can delete
		// a record or not
		E entity = read(id);
		beforeDelete(entity);
		repository().deleteById(id);
		afterDelete(entity);
	}

	@Transactional
	@PreAuthorize("this.canDelete(authentication, #ids)")
	public void deleteMultiple(@NonNull Iterable<I> ids) {
		SearchFilters<E> idsFilters = new SearchFilters<>();
		Streams.stream(ids).filter(Objects::nonNull)
				.forEach(i -> idsFilters.or(getIdField(), SearchFilterOperator.IS_EQUAL, i));
		List<E> entities = readMultiple(idsFilters, null).getContent();
		entities.stream().forEach(this::beforeDelete);
		repository().deleteAll();
		entities.stream().forEach(this::afterDelete);
	}
}
