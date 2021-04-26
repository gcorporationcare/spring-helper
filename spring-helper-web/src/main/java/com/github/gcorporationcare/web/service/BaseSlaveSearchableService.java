/**
 * 
 */
package com.github.gcorporationcare.web.service;

import java.io.Serializable;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.gcorporationcare.data.domain.SearchFilter;
import com.github.gcorporationcare.data.domain.SearchFilter.SearchFilterOperator;
import com.github.gcorporationcare.data.domain.SearchFilters;
import com.github.gcorporationcare.data.entity.BaseEntity;
import com.github.gcorporationcare.data.i18n.I18nMessage;
import com.github.gcorporationcare.data.repository.BaseRepository;
import com.github.gcorporationcare.web.exception.RequestException;

import lombok.NonNull;

/**
 *
 */
@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
public abstract class BaseSlaveSearchableService<E extends BaseEntity, ID extends Serializable, R extends BaseRepository<E, ID> & PagingAndSortingRepository<E, ID>, M_ID extends Serializable>
		extends BaseService<E, ID, R> {

	public abstract String getMasterField();

	public SearchFilters<E> getMasterFilters(@NonNull M_ID masterId) {
		return SearchFilters.of(new SearchFilter(true, getMasterField(), SearchFilterOperator.IS_EQUAL, masterId));
	}

	@Transactional(readOnly = true)
	public E read(@NonNull M_ID masterId, ID id) {
		if (id == null)
			throw new RequestException(I18nMessage.RequestError.INVALID_GIVEN_PARAMETERS, HttpStatus.BAD_REQUEST);
		SearchFilters<E> filters = SearchFilters.of(getIdField(), SearchFilterOperator.IS_EQUAL, id);
		SearchFilters<E> defaultFilters = safeFilters(masterId, filters);
		E saved = repository().findOneByFilters(defaultFilters);
		if (saved == null)
			throw new RequestException(I18nMessage.RequestError.OBJECT_NOT_FOUND, HttpStatus.NOT_FOUND);
		return saved;
	}

	@Transactional(readOnly = true)
	public Page<E> readMultiple(@NonNull M_ID masterId, SearchFilters<E> filters, Pageable pageable) {
		SearchFilters<E> defaultFilters = safeFilters(masterId, filters);
		return repository().findByFilters(defaultFilters, pageable);
	}

	@Transactional(readOnly = true)
	public E readOne(@NonNull M_ID masterId, SearchFilters<E> filters) {
		SearchFilters<E> defaultFilters = safeFilters(masterId, filters);
		return repository().findOneByFilters(defaultFilters);
	}

	private SearchFilters<E> safeFilters(M_ID masterId, SearchFilters<E> filters) {
		SearchFilters<E> masterFilters = getMasterFilters(masterId);
		SearchFilters<E> defaultFilters = getDefaultFilters();
		if (defaultFilters == null || masterFilters == null)
			throw new RequestException(I18nMessage.RequestError.FORBIDDEN_OPERATION, HttpStatus.FORBIDDEN);
		defaultFilters = masterFilters.and(defaultFilters);
		if (filters != null)
			defaultFilters = defaultFilters.and(filters);
		return defaultFilters;
	}
}
