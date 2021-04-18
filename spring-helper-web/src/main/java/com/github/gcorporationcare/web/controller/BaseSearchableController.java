package com.github.gcorporationcare.web.controller;

import java.io.Serializable;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.gcorporationcare.data.domain.FieldFilter;
import com.github.gcorporationcare.data.domain.SearchFilters;
import com.github.gcorporationcare.data.entity.BaseEntity;
import com.github.gcorporationcare.data.i18n.I18nMessage;
import com.github.gcorporationcare.data.repository.BaseRepository;
import com.github.gcorporationcare.web.service.BaseSearchableService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import springfox.documentation.annotations.ApiIgnore;

@Slf4j
public abstract class BaseSearchableController<E extends BaseEntity, ID extends Serializable, R extends BaseRepository<E, ID> & PagingAndSortingRepository<E, ID>> {

	public abstract BaseSearchableService<E, ID, R> service();

	@ResponseBody
	@ApiImplicitParams({
			@ApiImplicitParam(name = I18nMessage.FIELDS_PARAMETER, dataTypeClass = String.class, paramType = I18nMessage.QUERY_PARAM_TYPE, value = I18nMessage.FIELDS_PARAMETER_DESCRIPTION) })
	@ApiOperation(value = "read-by-id", notes = "Read the object with the given ID")
	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public E read(@PathVariable(I18nMessage.ID_PARAMETER) ID id,
			@ApiIgnore(I18nMessage.IGNORE_PARAMETER) FieldFilter<E> fieldFilter) {
		log.info("Reading {} with id {}", service().getEntityClass().getSimpleName(), id);
		return service().read(id, fieldFilter);
	}

	@ResponseBody
	@ApiOperation(value = "read-by-filters", notes = "Find the objects where given filters are valid")
	@ApiImplicitParams({
			@ApiImplicitParam(name = I18nMessage.FIELDS_PARAMETER, dataTypeClass = String.class, paramType = I18nMessage.QUERY_PARAM_TYPE, value = I18nMessage.FIELDS_PARAMETER_DESCRIPTION),
			@ApiImplicitParam(name = I18nMessage.FILTERS_PARAMETER, dataTypeClass = String.class, paramType = I18nMessage.QUERY_PARAM_TYPE, value = I18nMessage.FILTERS_PARAMETER_DESCRIPTION),
			@ApiImplicitParam(name = I18nMessage.PAGE_PARAMETER, dataTypeClass = Integer.class, defaultValue = I18nMessage.PAGE_PARAMETER_DEFAULT, example = I18nMessage.PAGE_PARAMETER_DEFAULT, paramType = I18nMessage.QUERY_PARAM_TYPE, value = I18nMessage.PAGE_PARAMETER_DESCRIPTION),
			@ApiImplicitParam(name = I18nMessage.SIZE_PARAMETER, dataTypeClass = Integer.class, defaultValue = I18nMessage.SIZE_PARAMETER_DEFAULT, example = I18nMessage.SIZE_PARAMETER_DEFAULT, paramType = I18nMessage.QUERY_PARAM_TYPE, value = I18nMessage.SIZE_PARAMETER_DESCRIPTION),
			@ApiImplicitParam(name = I18nMessage.SORT_PARAMETER, allowMultiple = true, dataTypeClass = String.class, paramType = I18nMessage.QUERY_PARAM_TYPE, value = I18nMessage.SORT_PARAMETER_DESCRIPTION) })
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public Page<E> readMultiple(SearchFilters<E> filters,
			@ApiIgnore(I18nMessage.IGNORE_PARAMETER) FieldFilter<E> fieldFilter,
			@ApiIgnore(I18nMessage.IGNORE_PARAMETER) Pageable pageable) {
		log.info("Reading {} with filters {} at page {}", service().getEntityClass().getSimpleName(), filters,
				pageable);
		return service().readMultiple(filters, fieldFilter, pageable);
	}

	@ResponseBody
	@ApiOperation(value = "read-first-by-filters", notes = "Find the first object where given filters are valid")
	@ApiImplicitParams({
			@ApiImplicitParam(name = I18nMessage.FIELDS_PARAMETER, dataTypeClass = String.class, paramType = I18nMessage.QUERY_PARAM_TYPE, value = I18nMessage.FIELDS_PARAMETER_DESCRIPTION),
			@ApiImplicitParam(name = I18nMessage.FILTERS_PARAMETER, dataTypeClass = String.class, paramType = I18nMessage.QUERY_PARAM_TYPE, value = I18nMessage.FILTERS_PARAMETER_DESCRIPTION) })
	@GetMapping(value = "/first", produces = MediaType.APPLICATION_JSON_VALUE)
	public E readOne(SearchFilters<E> filters, @ApiIgnore(I18nMessage.IGNORE_PARAMETER) FieldFilter<E> fieldFilter) {
		log.info("Reading first {} with filters {}", service().getEntityClass().getSimpleName(), filters);
		return service().readOne(filters, fieldFilter);
	}

}