package com.gcorp.service;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import com.gcorp.common.Utils;
import com.gcorp.domain.FieldFilter;
import com.gcorp.domain.SearchFilter.SearchFilterOperator;
import com.gcorp.domain.SearchFilters;
import com.gcorp.entity.BaseEntity;
import com.gcorp.exception.RequestException;
import com.gcorp.exception.StandardRuntimeException;
import com.gcorp.i18n.I18nMessage;
import com.gcorp.repository.BaseRepository;
import com.google.common.collect.Streams;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional(propagation = Propagation.REQUIRED)
public abstract class BaseChildRegistrableService<E extends BaseEntity, ID extends Serializable, P_E extends BaseEntity, P_ID extends Serializable, R extends BaseRepository<E, ID> & PagingAndSortingRepository<E, ID>, P_R extends BaseRepository<P_E, P_ID> & PagingAndSortingRepository<P_E, P_ID>>
		extends BaseChildSearchableService<E, ID, P_ID, R> {

	public abstract R repository();

	public abstract P_R parentRepository();

	public abstract String getParentField();

	public boolean isHardDelete() {
		return true;
	}

	public String softDeleteField(E child) {
		log.debug("Child {} can be used to choose the right field", child);
		return getParentField();
	}

	@SuppressWarnings("unchecked")
	public ID getFieldOfChild(@NonNull String fieldName, @NonNull E child) {
		try {
			Field field = child.getClass().getDeclaredField(fieldName);
			ReflectionUtils.makeAccessible(field);
			return (ID) field.get(child);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			throw new StandardRuntimeException(e);
		}
	}

	public P_E getParent(@NonNull P_ID id) {
		Optional<P_E> parent = parentRepository().findById(id);
		if (!parent.isPresent())
			throw new RequestException(I18nMessage.RequestError.OBJECT_NOT_FOUND, HttpStatus.NOT_FOUND,
					getParentIdField(), id);
		return parent.get();
	}

	public E getChild(ID id) {
		Optional<E> child = repository().findById(id);
		if (!child.isPresent())
			throw new RequestException(I18nMessage.RequestError.OBJECT_NOT_FOUND, HttpStatus.NOT_FOUND, getIdField(),
					id);
		return child.get();
	}

	public abstract void canCreate(@NonNull P_ID parentId, @NonNull E child);

	protected void afterCreate(E entity) {
		log.info("Created {}", entity);
	}

	@Transactional
	public E create(@NonNull P_ID parentId, @NonNull E child, @NonNull FieldFilter<E> fieldFilter) {
		P_E parent = getParent(parentId);
		Utils.setFieldValue(getParentField(), child, BaseEntity.class, parent);
		canCreate(parentId, child);
		E saved = repository().save(child);
		afterCreate(saved);
		return fieldFilter.parseEntity(saved);
	}

	@Transactional
	public Iterable<E> createMultiple(@NonNull P_ID parentId, Iterable<E> children,
			@NonNull FieldFilter<E> fieldFilter) {
		P_E parent = getParent(parentId);
		Streams.stream(children).forEach(c -> {
			Utils.setFieldValue(getParentField(), c, BaseEntity.class, parent);
			canCreate(parentId, c);
		});
		Iterable<E> saved = repository().saveAll(children);
		StreamSupport.stream(saved.spliterator(), false).forEach(this::afterCreate);
		return fieldFilter.parseIterable(saved);
	}

	public abstract void canUpdate(@NonNull P_ID parentId, @NonNull E child, @NonNull E savedChild);

	private E merge(@NonNull P_ID parentId, @NonNull ID childId, @NonNull E child, boolean excludeNull) {
		E saved = read(parentId, childId, FieldFilter.allFields());
		Utils.setFieldValue(getIdField(), child, BaseEntity.class, childId);
		final String parentField = getParentField();
		String[] excludedFields = excludeNull ? Utils.getNullPropertyNames(child) : null;
		Utils.setFieldValue(parentField, child, BaseEntity.class,
				Utils.getFieldValue(parentField, saved, BaseEntity.class));
		canUpdate(parentId, child, saved);
		saved.merge(child, excludedFields);
		return repository().save(saved);
	}

	protected void afterUpdate(E entity) {
		log.info("Updated {}", entity);
	}

	@Transactional
	public E update(@NonNull P_ID parentId, @NonNull ID childId, @NonNull E child,
			@NonNull FieldFilter<E> fieldFilter) {
		E saved = merge(parentId, childId, child, false);
		afterUpdate(saved);
		return fieldFilter.parseEntity(saved);
	}

	@Transactional
	public E patch(@NonNull P_ID parentId, @NonNull ID childId, @NonNull E child, @NonNull FieldFilter<E> fieldFilter) {
		E saved = merge(parentId, childId, child, true);
		afterUpdate(saved);
		return fieldFilter.parseEntity(saved);
	}

	public abstract void canDelete(@NonNull P_ID parentId, @NonNull E child);

	@Transactional
	public void delete(@NonNull P_ID parentId, @NonNull ID childId) {
		E child = read(parentId, childId, FieldFilter.allFields());
		canDelete(parentId, child);
		if (isHardDelete())
			repository().delete(child);
		else {
			Utils.setFieldValue(softDeleteField(child), child, BaseEntity.class, null);
			repository().save(child);
		}
	}

	@Transactional
	public void deleteMultiple(@NonNull P_ID parentId, @NonNull Iterable<ID> ids) {
		SearchFilters<E> idsFilters = new SearchFilters<>();
		Streams.stream(ids).filter(i -> i != null)
				.forEach(i -> idsFilters.or(getIdField(), SearchFilterOperator.IS_EQUAL, i));
		List<E> children = repository().findByFilters(idsFilters, null).getContent();
		children.stream().forEach(c -> canDelete(parentId, c));
		if (isHardDelete())
			repository().deleteAll(children);
		else {
			children.stream().forEach(c -> Utils.setFieldValue(softDeleteField(c), c, BaseEntity.class, null));
			repository().saveAll(children);
		}
		repository().deleteAll(children);
	}
}
