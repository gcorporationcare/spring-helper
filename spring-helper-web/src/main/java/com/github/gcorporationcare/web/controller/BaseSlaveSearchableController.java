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
import com.github.gcorporationcare.web.annotation.ApiIdentifiableEndpoint;
import com.github.gcorporationcare.web.annotation.ApiPageableEndpoint;
import com.github.gcorporationcare.web.domain.FieldFilter;
import com.github.gcorporationcare.web.dto.BaseDto;
import com.github.gcorporationcare.web.i18n.ParameterKey;
import com.github.gcorporationcare.web.service.BaseSlaveSearchableService;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import springfox.documentation.annotations.ApiIgnore;

@Slf4j
public abstract class BaseSlaveSearchableController<D extends BaseDto, E extends BaseEntity, ID extends Serializable, R extends BaseRepository<E, ID> & PagingAndSortingRepository<E, ID>, M_ID extends Serializable>
		extends BaseController<D, E> {

	public abstract BaseSlaveSearchableService<E, ID, R, M_ID> service();

	@ResponseBody
	@ApiIdentifiableEndpoint
	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "read-by-id", notes = "Read the object with the given ID")
	public D read(@PathVariable(ParameterKey.MASTER_PARAMETER) M_ID masterId,
			@PathVariable(ParameterKey.ID_PARAMETER) ID id,
			@ApiIgnore(ParameterKey.IGNORE_PARAMETER_REASON) FieldFilter<D> fieldFilter) {
		log.info("Reading {} with id {} for master {}", service().getEntityClass().getSimpleName(), id, masterId);
		E entity = service().read(masterId, id);
		return mapToDto(entity, fieldFilter);
	}

	@ResponseBody
	@ApiPageableEndpoint
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "read-by-filters", notes = "Find the objects where given filters are valid")
	public Page<D> readMultiple(@PathVariable(ParameterKey.MASTER_PARAMETER) M_ID masterId, SearchFilters<E> filters,
			@ApiIgnore(ParameterKey.IGNORE_PARAMETER_REASON) FieldFilter<D> fieldFilter,
			@ApiIgnore(ParameterKey.IGNORE_PARAMETER_REASON) Pageable pageable) {
		log.info("Reading {} with filters {} at page {} for master {}", service().getEntityClass().getSimpleName(),
				filters, pageable, masterId);
		Page<E> page = service().readMultiple(masterId, filters, pageable);
		return mapToPageDto(page, fieldFilter);
	}

	@ResponseBody
	@ApiFilterableEndpoint
	@GetMapping(value = "/first", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "read-first-by-filters", notes = "Find the first object where given filters are valid")
	public D readOne(@PathVariable(ParameterKey.MASTER_PARAMETER) M_ID masterId, SearchFilters<E> filters,
			@ApiIgnore(ParameterKey.IGNORE_PARAMETER_REASON) FieldFilter<D> fieldFilter) {
		log.info("Reading first {} with filters {} for master {}", service().getEntityClass().getSimpleName(), filters,
				masterId);
		E entity = service().readOne(masterId, filters);
		return mapToDto(entity, fieldFilter);
	}

}
