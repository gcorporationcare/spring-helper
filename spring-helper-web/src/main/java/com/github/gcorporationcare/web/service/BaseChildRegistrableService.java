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
public abstract class BaseChildRegistrableService<E extends BaseEntity, I extends Serializable, P extends Serializable, R extends BaseRepository<E, I> & PagingAndSortingRepository<E, I>, V extends Serializable>
		extends BaseChildSearchableService<E, I, P, R> {

	/**
	 * Used to check if user have required access to create data from this service.
	 * 
	 * @param authentication the current authentication object from security context
	 * @param parent         the parent ID
	 * @param entity         the record to save
	 * 
	 * @return true if user is allowed
	 */
	public boolean canCreate(Authentication authentication, P parent, E entity) {
		return true;
	}

	/**
	 * Used to check if user have required access to create data from this service.
	 * 
	 * @param authentication the current authentication object from security context
	 * @param parent         the parent ID
	 * @param entities       the records to save
	 * 
	 * @return true if user is allowed
	 */
	public final boolean canCreate(Authentication authentication, P parent, Iterable<E> entities) {
		return Streams.stream(entities).filter(e -> !canCreate(authentication, parent, e)).count() == 0;
	}

	/**
	 * Used to check if user have required access to update data from this service.
	 * 
	 * @param authentication the current authentication object from security context
	 * @param parent         the parent ID
	 * @param id             the record ID
	 * @param entity         the record to save
	 * 
	 * @return true if user is allowed
	 */
	public boolean canUpdate(Authentication authentication, P parent, I id, E entity) {
		return true;
	}

	/**
	 * Used to check if user have required access to delete data from this service.
	 * 
	 * @param authentication the current authentication object from security context
	 * @param parent         the parent ID
	 * @param id             the record ID
	 * 
	 * @return true if user is allowed
	 */
	public boolean canDelete(Authentication authentication, P parent, I id) {
		return true;
	}

	/**
	 * Used to check if user have required access to delete data from this service.
	 * 
	 * @param authentication the current authentication object from security context
	 * @param parent         the parent ID
	 * @param ids            the record IDs
	 * 
	 * @return true if user is allowed
	 */
	public final boolean canDelete(Authentication authentication, P parent, Iterable<I> ids) {
		return Streams.stream(ids).filter(id -> !canDelete(authentication, parent, id)).count() == 0;
	}

	public abstract String getParentField();

	public boolean isHardDelete() {
		return true;
	}

	public String softDeleteField(E child) {
		log.debug("Child {} can be used to choose the right field", child);
		return getParentField();
	}

	private V getParent(@NonNull P parentId) {
		return findParent(parentId).orElseThrow(() -> new RequestException(I18nMessage.RequestError.OBJECT_NOT_FOUND,
				HttpStatus.NOT_FOUND, getParentIdField(), parentId));
	}

	public abstract Optional<V> findParent(@NonNull P parentId);

	/**
	 * Do some action before creating
	 * 
	 * @param parentId the ID of parent record
	 * @param entity   the entity to save
	 * @throws RequestException when something is off with given data
	 */
	protected void beforeCreate(@NonNull P parentId, @NonNull E entity) {
		log.debug("Attempting to create {} for parentId {}", entity, parentId);
	}

	/**
	 * Do some action after data has been created
	 * 
	 * @param parentId the ID of parent record
	 * @param entity   the created data
	 */
	protected void afterCreate(@NonNull P parentId, @NonNull E entity) {
		log.debug("Created {} for parentId {}", entity, parentId);
	}

	@Transactional
	@PreAuthorize("this.canCreate(authentication, #parentId, #entity)")
	public E create(@NonNull P parentId, @NonNull E child) {
		V parent = getParent(parentId);
		Utils.setFieldValue(getParentField(), child, BaseEntity.class, parent);
		beforeCreate(parentId, child);
		E saved = repository().save(child);
		afterCreate(parentId, saved);
		return saved;
	}

	@Transactional
	@PreAuthorize("this.canCreate(authentication, #parentId, #entities)")
	public Iterable<E> createMultiple(@NonNull P parentId, Iterable<E> entities) {
		V parent = getParent(parentId);
		Streams.stream(entities).forEach(c -> {
			Utils.setFieldValue(getParentField(), c, BaseEntity.class, parent);
			beforeCreate(parentId, c);
		});
		Iterable<E> saved = repository().saveAll(entities);
		Streams.stream(saved).forEach(s -> afterCreate(parentId, s));
		return saved;
	}

	/**
	 * Do some action before updating
	 * 
	 * @param parentId the ID of parent record
	 * @param entity   the entity to save
	 * @throws RequestException when something is off with given data
	 */
	protected void beforeUpdate(@NonNull P parentId, @NonNull E entity, @NonNull E savedEntity, boolean patching) {
		log.debug("Attempting to update {} for parentId", entity, parentId);
	}

	/**
	 * Do some action after data has been updated
	 * 
	 * @param parentId the ID of parent record
	 * @param entity   the updated data
	 */
	protected void afterUpdate(@NonNull P parentId, @NonNull E entity) {
		log.debug("Updated {} for parentId", entity, parentId);
	}

	private E merge(@NonNull P parentId, @NonNull I childId, @NonNull E entity, boolean patching) {
		E saved = read(parentId, childId);
		Utils.setFieldValue(getIdField(), entity, BaseEntity.class, childId);
		final String parentField = getParentField();
		String[] excludedFields = patching ? Utils.getNullPropertyNames(entity) : null;
		Utils.setFieldValue(parentField, entity, BaseEntity.class,
				Utils.getFieldValue(parentField, saved, BaseEntity.class));
		beforeUpdate(parentId, entity, saved, patching);
		saved.merge(entity, excludedFields);
		saved = repository().save(saved);
		afterUpdate(parentId, saved);
		return saved;
	}

	@Transactional
	@PreAuthorize("this.canUpdate(authentication, #parentId, #childId, #entity)")
	public E update(@NonNull P parentId, @NonNull I childId, @NonNull E entity) {
		return merge(parentId, childId, entity, false);
	}

	@Transactional
	@PreAuthorize("this.canUpdate(authentication, #parentId, #childId, #entity)")
	public E patch(@NonNull P parentId, @NonNull I childId, @NonNull E entity) {
		return merge(parentId, childId, entity, true);
	}

	/**
	 * Do some action before deleting
	 * 
	 * @param parentId the ID of parent record
	 * @param entity   the entity to remove
	 * @throws RequestException when something is off with given data
	 */
	protected void beforeDelete(@NonNull P parentId, @NonNull E entity) {
		log.debug("Attempting to delete {} for parentId {}", entity, parentId);
	}

	/**
	 * Do some action after data has been deleted
	 * 
	 * @param parentId the ID of parent record
	 * @param entity   the removed data
	 */
	protected void afterDelete(@NonNull P parentId, @NonNull E entity) {
		log.debug("Deleted {}", entity);
	}

	@Transactional
	@PreAuthorize("this.canDelete(authentication, #parentId, #childId)")
	public void delete(@NonNull P parentId, @NonNull I childId) {
		E entity = read(parentId, childId);
		beforeDelete(parentId, entity);
		if (isHardDelete())
			repository().delete(entity);
		else {
			Utils.setFieldValue(softDeleteField(entity), entity, BaseEntity.class, null);
			repository().save(entity);
		}
		afterDelete(parentId, entity);
	}

	@Transactional
	@PreAuthorize("this.canDelete(authentication, #parentId, #ids)")
	public void deleteMultiple(@NonNull P parentId, @NonNull Iterable<I> ids) {
		SearchFilters<E> idsFilters = new SearchFilters<>();
		Streams.stream(ids).filter(Objects::nonNull)
				.forEach(i -> idsFilters.or(getIdField(), SearchFilterOperator.IS_EQUAL, i));
		List<E> entities = repository().findByFilters(idsFilters, null).getContent();
		entities.stream().forEach(e -> beforeDelete(parentId, e));
		if (isHardDelete())
			repository().deleteAll(entities);
		else {
			entities.stream().forEach(c -> Utils.setFieldValue(softDeleteField(c), c, BaseEntity.class, null));
			repository().saveAll(entities);
		}
		entities.stream().forEach(e -> afterDelete(parentId, e));
	}
}
