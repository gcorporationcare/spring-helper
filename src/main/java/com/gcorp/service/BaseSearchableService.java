/**
 * 
 */
package com.gcorp.service;

import java.io.Serializable;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.gcorp.common.Utils;
import com.gcorp.domain.FieldFilter;
import com.gcorp.domain.SearchFilter;
import com.gcorp.domain.SearchFilter.SearchFilterOperator;
import com.gcorp.domain.SearchFilters;
import com.gcorp.entity.BaseEntity;
import com.gcorp.exception.RequestException;
import com.gcorp.i18n.I18nMessage;
import com.gcorp.repository.BaseRepository;

import lombok.NonNull;

/**
 *
 */
@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
public abstract class BaseSearchableService<E extends BaseEntity, ID extends Serializable, R extends BaseRepository<E, ID> & PagingAndSortingRepository<E, ID>> {

	public static final String ID_FIELD = "id";

	public abstract R repository();

	@Autowired
	protected ServletContext servletContext;

	@Autowired
	protected HttpServletResponse response;

	@Autowired
	protected HttpServletRequest request;

	/**
	 * Will be useful when logging message of different services/controllers
	 */
	@SuppressWarnings("unchecked")
	public Class<E> getEntityClass() {
		return (Class<E>) Utils.getParameterizedType(getClass());
	}

	public String getIdField() {
		return ID_FIELD;
	}

	public SearchFilters<E> getDefaultFilters() {
		return SearchFilters.of(new SearchFilter(true, getIdField(), SearchFilterOperator.IS_NOT_NULL));
	}

	@Transactional(readOnly = true)
	public E read(ID id, @NonNull FieldFilter<E> fieldFilter) {
		if (id == null)
			throw new RequestException(I18nMessage.RequestError.INVALID_GIVEN_PARAMETERS, HttpStatus.BAD_REQUEST);
		SearchFilters<E> filters = SearchFilters.of(getIdField(), SearchFilterOperator.IS_EQUAL, id);
		SearchFilters<E> defaultFilters = safeFilters(filters);
		E saved = repository().findOneByFilters(defaultFilters);
		if (saved == null)
			throw new RequestException(I18nMessage.RequestError.OBJECT_NOT_FOUND, HttpStatus.NOT_FOUND);
		fieldFilter.setToAllIfEmpty();
		return fieldFilter.parseEntity(saved);
	}

	@Transactional(readOnly = true)
	public Page<E> readMultiple(SearchFilters<E> filters, @NonNull FieldFilter<E> fieldFilter, Pageable pageable) {
		SearchFilters<E> defaultFilters = safeFilters(filters);
		Page<E> page = repository().findByFilters(defaultFilters, pageable);
		return fieldFilter.parsePage(page);
	}

	@Transactional(readOnly = true)
	public E readOne(SearchFilters<E> filters, @NonNull FieldFilter<E> fieldFilter) {
		SearchFilters<E> defaultFilters = safeFilters(filters);
		E entity = repository().findOneByFilters(defaultFilters);
		fieldFilter.setToAllIfEmpty();
		return fieldFilter.parseEntity(entity);
	}

	private SearchFilters<E> safeFilters(SearchFilters<E> filters) {
		SearchFilters<E> defaultFilters = getDefaultFilters();
		if (defaultFilters == null)
			throw new RequestException(I18nMessage.RequestError.UNSUFFICIENT_RIGHTS, HttpStatus.FORBIDDEN);
		if (filters != null)
			defaultFilters = defaultFilters.and(filters);
		return defaultFilters;
	}
}
