package com.github.gcorporationcare.web.service;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;

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

	public abstract void checkForCreate(@NonNull P parentId, @NonNull E child);

	protected void afterCreate(@NonNull E entity) {
		log.info("Created {}", entity);
	}

	@Transactional
	@PreAuthorize("this.canCreate(authentication, #parentId, #entity)")
	public E create(@NonNull P parentId, @NonNull E child) {
		V parent = getParent(parentId);
		Utils.setFieldValue(getParentField(), child, BaseEntity.class, parent);
		checkForCreate(parentId, child);
		E saved = repository().save(child);
		afterCreate(saved);
		return saved;
	}

	@Transactional
	@PreAuthorize("this.canCreate(authentication, #parentId, #entities)")
	public Iterable<E> createMultiple(@NonNull P parentId, Iterable<E> entities) {
		V parent = getParent(parentId);
		Streams.stream(entities).forEach(c -> {
			Utils.setFieldValue(getParentField(), c, BaseEntity.class, parent);
			checkForCreate(parentId, c);
		});
		Iterable<E> saved = repository().saveAll(entities);
		StreamSupport.stream(saved.spliterator(), false).forEach(this::afterCreate);
		return saved;
	}

	public abstract void checkForUpdate(@NonNull P parentId, @NonNull E child, @NonNull E savedChild);

	private E merge(@NonNull P parentId, @NonNull I childId, @NonNull E entity, boolean excludeNull) {
		E saved = read(parentId, childId);
		Utils.setFieldValue(getIdField(), entity, BaseEntity.class, childId);
		final String parentField = getParentField();
		String[] excludedFields = excludeNull ? Utils.getNullPropertyNames(entity) : null;
		Utils.setFieldValue(parentField, entity, BaseEntity.class,
				Utils.getFieldValue(parentField, saved, BaseEntity.class));
		checkForUpdate(parentId, entity, saved);
		saved.merge(entity, excludedFields);
		return repository().save(saved);
	}

	protected void afterUpdate(@NonNull E entity) {
		log.info("Updated {}", entity);
	}

	@Transactional
	@PreAuthorize("this.canUpdate(authentication, #parentId, #childId, #entity)")
	public E update(@NonNull P parentId, @NonNull I childId, @NonNull E entity) {
		E saved = merge(parentId, childId, entity, false);
		afterUpdate(saved);
		return saved;
	}

	@Transactional
	@PreAuthorize("this.canUpdate(authentication, #parentId, #childId, #entity)")
	public E patch(@NonNull P parentId, @NonNull I childId, @NonNull E entity) {
		E saved = merge(parentId, childId, entity, true);
		afterUpdate(saved);
		return saved;
	}

	public abstract void checkForDelete(@NonNull P parentId, @NonNull E child);

	@Transactional
	@PreAuthorize("this.canDelete(authentication, #parentId, #childId)")
	public void delete(@NonNull P parentId, @NonNull I childId) {
		E child = read(parentId, childId);
		checkForDelete(parentId, child);
		if (isHardDelete())
			repository().delete(child);
		else {
			Utils.setFieldValue(softDeleteField(child), child, BaseEntity.class, null);
			repository().save(child);
		}
	}

	@Transactional
	@PreAuthorize("this.canDelete(authentication, #parentId, #ids)")
	public void deleteMultiple(@NonNull P parentId, @NonNull Iterable<I> ids) {
		SearchFilters<E> idsFilters = new SearchFilters<>();
		Streams.stream(ids).filter(Objects::nonNull)
				.forEach(i -> idsFilters.or(getIdField(), SearchFilterOperator.IS_EQUAL, i));
		List<E> children = repository().findByFilters(idsFilters, null).getContent();
		children.stream().forEach(c -> checkForDelete(parentId, c));
		if (isHardDelete())
			repository().deleteAll(children);
		else {
			children.stream().forEach(c -> Utils.setFieldValue(softDeleteField(c), c, BaseEntity.class, null));
			repository().saveAll(children);
		}
	}
}
