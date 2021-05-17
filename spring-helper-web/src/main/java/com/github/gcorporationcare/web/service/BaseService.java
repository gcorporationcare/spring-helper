package com.github.gcorporationcare.web.service;

import java.io.Serializable;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.github.gcorporationcare.data.common.Utils;
import com.github.gcorporationcare.data.domain.SearchFilter;
import com.github.gcorporationcare.data.domain.SearchFilters;
import com.github.gcorporationcare.data.domain.SearchFilter.SearchFilterOperator;
import com.github.gcorporationcare.data.entity.BaseEntity;
import com.github.gcorporationcare.data.repository.BaseRepository;

public abstract class BaseService<E extends BaseEntity, I extends Serializable, R extends BaseRepository<E, I> & PagingAndSortingRepository<E, I>> {
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
}
