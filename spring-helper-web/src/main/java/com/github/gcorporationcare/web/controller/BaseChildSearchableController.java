package com.github.gcorporationcare.web.controller;

import java.io.Serializable;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.gcorporationcare.data.domain.SearchFilters;
import com.github.gcorporationcare.data.entity.BaseEntity;
import com.github.gcorporationcare.data.repository.BaseRepository;
import com.github.gcorporationcare.web.annotation.ApiFilterableEndpoint;
import com.github.gcorporationcare.web.annotation.ApiPageableEndpoint;
import com.github.gcorporationcare.web.annotation.ApiSimpleEndpoint;
import com.github.gcorporationcare.web.common.ParameterKey;
import com.github.gcorporationcare.web.domain.FieldFilter;
import com.github.gcorporationcare.web.dto.BaseDto;
import com.github.gcorporationcare.web.service.BaseChildSearchableService;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import springfox.documentation.annotations.ApiIgnore;

@Slf4j
public abstract class BaseChildSearchableController<D extends BaseDto, E extends BaseEntity, I extends Serializable, R extends BaseRepository<E, I> & PagingAndSortingRepository<E, I>, P extends Serializable>
		extends BaseController<D, E> {

	public abstract BaseChildSearchableService<E, I, R, P> service();

	@ResponseBody
	@ApiSimpleEndpoint
	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "read-by-id", notes = "Read the object with the given ID")
	public D read(@PathVariable(ParameterKey.PARENT_PARAMETER) P parent, @PathVariable(ParameterKey.ID_PARAMETER) I id,
			@ApiIgnore(ParameterKey.IGNORE_PARAMETER_REASON) FieldFilter<D> fieldFilter) {
		log.info("Reading {} from parent {} with id {}", service().getEntityClass().getSimpleName(), parent, id);
		E entity = service().read(parent, id);
		return mapToDto(entity, fieldFilter);
	}

	@ResponseBody
	@ApiPageableEndpoint
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "read-multiple-by-filters", notes = "Find the objects where given filters are valid")
	public Page<D> readMultiple(@PathVariable(ParameterKey.PARENT_PARAMETER) P parent, SearchFilters<E> filters,
			@ApiIgnore(ParameterKey.IGNORE_PARAMETER_REASON) FieldFilter<D> fieldFilter,
			@ApiIgnore(ParameterKey.IGNORE_PARAMETER_REASON) Pageable pageable) {
		log.info("Reading first {} with filters {}", service().getEntityClass().getSimpleName(), filters);
		Page<E> page = service().readMultiple(parent, filters, pageable);
		return mapToPageDto(page, fieldFilter);
	}

	@ResponseBody
	@ApiFilterableEndpoint
	@GetMapping(value = "/first", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "read-one-by-filters", notes = "Find the first object where given filters are valid")
	public D readOne(@PathVariable(ParameterKey.PARENT_PARAMETER) P parent, SearchFilters<E> filters,
			@ApiIgnore(ParameterKey.IGNORE_PARAMETER_REASON) FieldFilter<D> fieldFilter) {
		log.info("Reading first {} from parent {} with filters {}", service().getEntityClass().getSimpleName(),
				filters);
		E entity = service().readOne(parent, filters);
		return mapToDto(entity, fieldFilter);
	}
}
