package com.github.gcorporationcare.web.service;

import java.io.Serializable;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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

@PreAuthorize("this.canRead(authentication, #parent)")
@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
public abstract class BaseChildSearchableService<E extends BaseEntity, I extends Serializable, P extends Serializable, R extends BaseRepository<E, I> & PagingAndSortingRepository<E, I>>
		extends BaseService<E, I, R> {

	public abstract String getParentIdField();

	/**
	 * Used to check if user have required access to read data from this service
	 * methods. The filtering of the record the user can see is dealt with via
	 * getDefaultFilters
	 * 
	 * @param authentication the current authentication object from security context
	 * @param parent         the parent ID
	 * @return true if user is allowed
	 */
	public boolean canRead(Authentication authentication, P parent) {
		return !Objects.isNull(parent);
	}

	public SearchFilters<E> getParentFilters(@NonNull P id) {
		SearchFilters<E> defaultFilters = getDefaultFilters();
		if (defaultFilters == null)
			throw new RequestException(I18nMessage.RequestError.FORBIDDEN_OPERATION, HttpStatus.FORBIDDEN);
		SearchFilters<E> parentFilters = SearchFilters
				.of(new SearchFilter(true, getParentIdField(), SearchFilterOperator.IS_EQUAL, id));
		return parentFilters.and(defaultFilters);
	}

	@Transactional(readOnly = true)
	public E read(@NonNull P parentId, I id) {
		if (id == null)
			throw new RequestException(I18nMessage.RequestError.INVALID_GIVEN_PARAMETERS, HttpStatus.BAD_REQUEST);
		SearchFilters<E> filters = getParentFilters(parentId).and(getIdField(), SearchFilterOperator.IS_EQUAL, id);
		E saved = repository().findOneByFilters(filters);
		if (saved == null)
			throw new RequestException(I18nMessage.RequestError.OBJECT_NOT_FOUND, HttpStatus.NOT_FOUND);
		return saved;
	}

	@Transactional(readOnly = true)
	public Page<E> readMultiple(@NonNull P parentId, SearchFilters<E> filters, Pageable pageable) {
		SearchFilters<E> parentFilters = safeParentFilters(parentId, filters);
		return repository().findByFilters(parentFilters, pageable);
	}

	@Transactional(readOnly = true)
	public E readOne(@NonNull P parentId, SearchFilters<E> filters) {
		SearchFilters<E> parentFilters = safeParentFilters(parentId, filters);
		return repository().findOneByFilters(parentFilters);
	}

	private SearchFilters<E> safeParentFilters(@NonNull P parentId, SearchFilters<E> filters) {
		SearchFilters<E> parentFilters = getParentFilters(parentId);
		if (filters != null)
			parentFilters = parentFilters.and(filters);
		return parentFilters;
	}
}
