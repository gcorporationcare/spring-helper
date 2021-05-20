package com.github.gcorporationcare.web.service;

import java.io.Serializable;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.validation.annotation.Validated;

import com.github.gcorporationcare.data.entity.BaseEntity;
import com.github.gcorporationcare.data.repository.BaseRepository;
import com.github.gcorporationcare.web.constraint.ValidationStep.OnCreate;
import com.github.gcorporationcare.web.constraint.ValidationStep.OnPatch;
import com.github.gcorporationcare.web.constraint.ValidationStep.OnUpdate;
import com.github.gcorporationcare.web.exception.RequestException;

import lombok.NonNull;

public interface BaseCrudService<E extends BaseEntity, I extends Serializable, R extends BaseRepository<E, I> & PagingAndSortingRepository<E, I>> {
	/**
	 * Do some action before creating. All Default and OnCreate constraints will be
	 * verified.
	 * 
	 * @param entity the entity to save
	 * @throws RequestException when something is off with given data
	 */
	default void beforeCreate(@Validated({ OnCreate.class }) E entity) throws RequestException {
		// Override if needed
	}

	/**
	 * Do some action after data has been created
	 * 
	 * @param entity the created data
	 */
	default void afterCreate(@NonNull E entity) {
		// Override if needed
	}

	/**
	 * Do some action before updating. All Default, OnPatch and OnUpdate constraints
	 * will be verified.
	 * 
	 * @param entity the entity to save
	 * @throws RequestException when something is off with given data
	 */
	default void beforeUpdate(@Validated({ OnUpdate.class }) E entity, @NonNull E savedEntity) {
		// Override if needed
	}

	/**
	 * Do some action before patching. All Default and OnPatch constraints will be
	 * verified.
	 * 
	 * @param entity the entity to save
	 * @throws RequestException when something is off with given data
	 */
	default void beforePatch(@Validated({ OnPatch.class }) E entity, @NonNull E savedEntity) {
		// Override if needed
	}

	/**
	 * Do some action after data has been updated (or patched)
	 * 
	 * @param entity the updated data
	 */
	default void afterUpdate(@NonNull E entity) {
		// Override if needed
	}

	/**
	 * Do some action before deleting
	 * 
	 * @param entity the entity to remove
	 * @throws RequestException when something is off with given data
	 */
	default void beforeDelete(@NonNull E entity) {
		// Override if needed
	}

	/**
	 * Do some action after data has been deleted
	 * 
	 * @param entity the removed data
	 */
	default void afterDelete(@NonNull E entity) {
		// Override if needed
	}
}
