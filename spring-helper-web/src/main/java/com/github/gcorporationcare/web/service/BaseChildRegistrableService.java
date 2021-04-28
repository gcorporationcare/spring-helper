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
public abstract class BaseChildRegistrableService<E extends BaseEntity, I extends Serializable, P extends Serializable, R extends BaseRepository<E, I> & PagingAndSortingRepository<E, I>, V extends Serializable>
		extends BaseChildSearchableService<E, I, P, R> {

	public abstract R repository();

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

	public abstract void canCreate(@NonNull P parentId, @NonNull E child);

	protected void afterCreate(@NonNull E entity) {
		log.info("Created {}", entity);
	}

	@Transactional
	public E create(@NonNull P parentId, @NonNull E child) {
		V parent = getParent(parentId);
		Utils.setFieldValue(getParentField(), child, BaseEntity.class, parent);
		canCreate(parentId, child);
		E saved = repository().save(child);
		afterCreate(saved);
		return saved;
	}

	@Transactional
	public Iterable<E> createMultiple(@NonNull P parentId, Iterable<E> children) {
		V parent = getParent(parentId);
		Streams.stream(children).forEach(c -> {
			Utils.setFieldValue(getParentField(), c, BaseEntity.class, parent);
			canCreate(parentId, c);
		});
		Iterable<E> saved = repository().saveAll(children);
		StreamSupport.stream(saved.spliterator(), false).forEach(this::afterCreate);
		return saved;
	}

	public abstract void canUpdate(@NonNull P parentId, @NonNull E child, @NonNull E savedChild);

	private E merge(@NonNull P parentId, @NonNull I childId, @NonNull E child, boolean excludeNull) {
		E saved = read(parentId, childId);
		Utils.setFieldValue(getIdField(), child, BaseEntity.class, childId);
		final String parentField = getParentField();
		String[] excludedFields = excludeNull ? Utils.getNullPropertyNames(child) : null;
		Utils.setFieldValue(parentField, child, BaseEntity.class,
				Utils.getFieldValue(parentField, saved, BaseEntity.class));
		canUpdate(parentId, child, saved);
		saved.merge(child, excludedFields);
		return repository().save(saved);
	}

	protected void afterUpdate(@NonNull E entity) {
		log.info("Updated {}", entity);
	}

	@Transactional
	public E update(@NonNull P parentId, @NonNull I childId, @NonNull E child) {
		E saved = merge(parentId, childId, child, false);
		afterUpdate(saved);
		return saved;
	}

	@Transactional
	public E patch(@NonNull P parentId, @NonNull I childId, @NonNull E child) {
		E saved = merge(parentId, childId, child, true);
		afterUpdate(saved);
		return saved;
	}

	public abstract void canDelete(@NonNull P parentId, @NonNull E child);

	@Transactional
	public void delete(@NonNull P parentId, @NonNull I childId) {
		E child = read(parentId, childId);
		canDelete(parentId, child);
		if (isHardDelete())
			repository().delete(child);
		else {
			Utils.setFieldValue(softDeleteField(child), child, BaseEntity.class, null);
			repository().save(child);
		}
	}

	@Transactional
	public void deleteMultiple(@NonNull P parentId, @NonNull Iterable<I> ids) {
		SearchFilters<E> idsFilters = new SearchFilters<>();
		Streams.stream(ids).filter(Objects::nonNull)
				.forEach(i -> idsFilters.or(getIdField(), SearchFilterOperator.IS_EQUAL, i));
		List<E> children = repository().findByFilters(idsFilters, null).getContent();
		children.stream().forEach(c -> canDelete(parentId, c));
		if (isHardDelete())
			repository().deleteAll(children);
		else {
			children.stream().forEach(c -> Utils.setFieldValue(softDeleteField(c), c, BaseEntity.class, null));
			repository().saveAll(children);
		}
	}
}
