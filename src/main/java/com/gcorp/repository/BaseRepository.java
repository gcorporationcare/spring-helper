package com.gcorp.repository;

import java.io.Serializable;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.gcorp.domain.SearchFilters;
import com.gcorp.entity.BaseEntity;

public interface BaseRepository<T extends BaseEntity, U extends Serializable> {
	/**
	 * Find objects where searchFilters' conditions are satisfied
	 */
	Page<T> findByFilters(String searchFilters, Pageable pageable);

	/**
	 * Find objects where searchFilters' conditions are satisfied
	 */
	Page<T> findByFilters(SearchFilters<T> searchFilters, Pageable pageable);

	/**
	 * Find the first object where searchFilters' condition are satisfied
	 */
	T findOneByFilters(String searchFilters);

	/**
	 * Find the first object where searchFilters' condition are satisfied
	 */
	T findOneByFilters(SearchFilters<T> searchFilters);
}
