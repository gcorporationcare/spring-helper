package com.github.gcorporationcare.data.repository;

import java.io.Serializable;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.github.gcorporationcare.data.domain.SearchFilters;
import com.github.gcorporationcare.data.entity.BaseEntity;

/**
 * 
 *
 * @param <T> The entity representing the table in database
 * @param <U> The type of the primary key of the previous table
 */
public interface BaseRepository<T extends BaseEntity, U extends Serializable> {
	/**
	 * Find objects where searchFilters' conditions are satisfied
	 * 
	 * @param searchFilters the filters representing the specification (where) to
	 *                      apply on database querying
	 * @param pageable      the pagination information (page, size, sort)
	 * @return a page containing the records from database (empty content if none)
	 */
	Page<T> findByFilters(String searchFilters, Pageable pageable);

	/**
	 * Find objects where searchFilters' conditions are satisfied
	 * 
	 * @param searchFilters the filters representing the specification (where) to
	 *                      apply on database querying
	 * @param pageable      the pagination information (page, size, sort)
	 * @return a page containing the records from database (empty content if none)
	 */
	Page<T> findByFilters(SearchFilters<T> searchFilters, Pageable pageable);

	/**
	 * Find the first object where searchFilters' condition are satisfied
	 * 
	 * @param searchFilters the filters representing the specification (where) to
	 *                      apply on database querying
	 * @return the first object in database matching the search criteria (can be
	 *         null)
	 */
	T findOneByFilters(String searchFilters);

	/**
	 * Find the first object where searchFilters' condition are satisfied
	 * 
	 * @param searchFilters the filters representing the specification (where) to
	 *                      apply on database querying
	 * @return the first object in database matching the search criteria (can be
	 *         null)
	 */
	T findOneByFilters(SearchFilters<T> searchFilters);
}
