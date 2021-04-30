/**
 * 
 */
package com.github.gcorporationcare.web.service;

import java.io.Serializable;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.gcorporationcare.data.domain.SearchFilter.SearchFilterOperator;
import com.github.gcorporationcare.data.domain.SearchFilters;
import com.github.gcorporationcare.data.entity.BaseEntity;
import com.github.gcorporationcare.data.i18n.I18nMessage;
import com.github.gcorporationcare.data.repository.BaseRepository;
import com.github.gcorporationcare.web.exception.RequestException;

/**
 *
 */
@PreAuthorize("this.canRead(authentication)")
@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
public abstract class BaseSearchableService<E extends BaseEntity, I extends Serializable, R extends BaseRepository<E, I> & PagingAndSortingRepository<E, I>>
		extends BaseService<E, I, R> {

	/**
	 * Used to check if user have required access to read data from this service
	 * methods. The filtering of the record the user can see is dealt with via
	 * getDefaultFilters
	 * 
	 * @param authentication the current authentication object from security context
	 * @return true if user is allowed
	 */
	public boolean canRead(Authentication authentication) {
		return true;
	}

	@Transactional(readOnly = true)
	public E read(I id) {
		if (id == null)
			throw new RequestException(I18nMessage.RequestError.INVALID_GIVEN_PARAMETERS, HttpStatus.BAD_REQUEST);
		SearchFilters<E> filters = SearchFilters.of(getIdField(), SearchFilterOperator.IS_EQUAL, id);
		SearchFilters<E> defaultFilters = safeFilters(filters);
		E saved = repository().findOneByFilters(defaultFilters);
		if (saved == null)
			throw new RequestException(I18nMessage.RequestError.OBJECT_NOT_FOUND, HttpStatus.NOT_FOUND);
		return saved;
	}

	@Transactional(readOnly = true)
	public Page<E> readMultiple(SearchFilters<E> filters, Pageable pageable) {
		SearchFilters<E> defaultFilters = safeFilters(filters);
		return repository().findByFilters(defaultFilters, pageable);
	}

	@Transactional(readOnly = true)
	public E readOne(SearchFilters<E> filters) {
		SearchFilters<E> defaultFilters = safeFilters(filters);
		return repository().findOneByFilters(defaultFilters);
	}

	private SearchFilters<E> safeFilters(SearchFilters<E> filters) {
		SearchFilters<E> defaultFilters = getDefaultFilters();
		if (defaultFilters == null)
			throw new RequestException(I18nMessage.RequestError.FORBIDDEN_OPERATION, HttpStatus.FORBIDDEN);
		if (filters != null)
			defaultFilters = defaultFilters.and(filters);
		return defaultFilters;
	}
}
