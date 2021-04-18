package com.github.gcorporationcare.web.service;

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

import com.github.gcorporationcare.data.common.Utils;
import com.github.gcorporationcare.data.domain.FieldFilter;
import com.github.gcorporationcare.data.domain.SearchFilter;
import com.github.gcorporationcare.data.domain.SearchFilters;
import com.github.gcorporationcare.data.domain.SearchFilter.SearchFilterOperator;
import com.github.gcorporationcare.data.entity.BaseEntity;
import com.github.gcorporationcare.data.i18n.I18nMessage;
import com.github.gcorporationcare.data.repository.BaseRepository;
import com.github.gcorporationcare.web.exception.RequestException;

import lombok.NonNull;

@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
public abstract class BaseChildSearchableService<E extends BaseEntity, ID extends Serializable, P_ID extends Serializable, R extends BaseRepository<E, ID> & PagingAndSortingRepository<E, ID>> {

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
	 * 
	 * @return the entity targeted by this service
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

	public abstract String getParentIdField();

	public SearchFilters<E> getParentFilters(@NonNull P_ID id) {
		SearchFilters<E> defaultFilters = getDefaultFilters();
		if (defaultFilters == null)
			throw new RequestException(I18nMessage.RequestError.FORBIDDEN_OPERATION, HttpStatus.FORBIDDEN);
		SearchFilters<E> parentFilters = SearchFilters
				.of(new SearchFilter(true, getParentIdField(), SearchFilterOperator.IS_EQUAL, id));
		return parentFilters.and(defaultFilters);
	}

	@Transactional(readOnly = true)
	public E read(@NonNull P_ID parentId, ID id, @NonNull FieldFilter<E> fieldFilter) {
		if (id == null)
			throw new RequestException(I18nMessage.RequestError.INVALID_GIVEN_PARAMETERS, HttpStatus.BAD_REQUEST);
		SearchFilters<E> filters = getParentFilters(parentId).and(getIdField(), SearchFilterOperator.IS_EQUAL, id);
		E saved = repository().findOneByFilters(filters);
		if (saved == null)
			throw new RequestException(I18nMessage.RequestError.OBJECT_NOT_FOUND, HttpStatus.NOT_FOUND);
		fieldFilter.setToAllIfEmpty();
		return fieldFilter.parseEntity(saved);
	}

	@Transactional(readOnly = true)
	public Page<E> readMultiple(@NonNull P_ID parentId, SearchFilters<E> filters, @NonNull FieldFilter<E> fieldFilter,
			Pageable pageable) {
		SearchFilters<E> parentFilters = safeParentFilters(parentId, filters);
		Page<E> page = repository().findByFilters(parentFilters, pageable);
		return fieldFilter.parsePage(page);
	}

	@Transactional(readOnly = true)
	public E readOne(@NonNull P_ID parentId, SearchFilters<E> filters, @NonNull FieldFilter<E> fieldFilter) {
		SearchFilters<E> parentFilters = safeParentFilters(parentId, filters);
		E entity = repository().findOneByFilters(parentFilters);
		fieldFilter.setToAllIfEmpty();
		return fieldFilter.parseEntity(entity);
	}

	private SearchFilters<E> safeParentFilters(@NonNull P_ID parentId, SearchFilters<E> filters) {
		SearchFilters<E> parentFilters = getParentFilters(parentId);
		if (filters != null)
			parentFilters = parentFilters.and(filters);
		return parentFilters;
	}
}
