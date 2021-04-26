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

@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
public abstract class BaseChildSearchableService<E extends BaseEntity, ID extends Serializable, P_ID extends Serializable, R extends BaseRepository<E, ID> & PagingAndSortingRepository<E, ID>>
		extends BaseService<E, ID, R> {

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
	public E read(@NonNull P_ID parentId, ID id) {
		if (id == null)
			throw new RequestException(I18nMessage.RequestError.INVALID_GIVEN_PARAMETERS, HttpStatus.BAD_REQUEST);
		SearchFilters<E> filters = getParentFilters(parentId).and(getIdField(), SearchFilterOperator.IS_EQUAL, id);
		E saved = repository().findOneByFilters(filters);
		if (saved == null)
			throw new RequestException(I18nMessage.RequestError.OBJECT_NOT_FOUND, HttpStatus.NOT_FOUND);
		return saved;
	}

	@Transactional(readOnly = true)
	public Page<E> readMultiple(@NonNull P_ID parentId, SearchFilters<E> filters, Pageable pageable) {
		SearchFilters<E> parentFilters = safeParentFilters(parentId, filters);
		return repository().findByFilters(parentFilters, pageable);
	}

	@Transactional(readOnly = true)
	public E readOne(@NonNull P_ID parentId, SearchFilters<E> filters) {
		SearchFilters<E> parentFilters = safeParentFilters(parentId, filters);
		return repository().findOneByFilters(parentFilters);
	}

	private SearchFilters<E> safeParentFilters(@NonNull P_ID parentId, SearchFilters<E> filters) {
		SearchFilters<E> parentFilters = getParentFilters(parentId);
		if (filters != null)
			parentFilters = parentFilters.and(filters);
		return parentFilters;
	}
}
