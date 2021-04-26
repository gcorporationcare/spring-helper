package com.github.gcorporationcare.web.controller;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.github.gcorporationcare.data.constraint.ValidationStep;
import com.github.gcorporationcare.data.entity.BaseEntity;
import com.github.gcorporationcare.data.repository.BaseRepository;
import com.github.gcorporationcare.web.annotation.ApiIdentifiableEndpoint;
import com.github.gcorporationcare.web.annotation.ApiSimpleEndpoint;
import com.github.gcorporationcare.web.domain.FieldFilter;
import com.github.gcorporationcare.web.dto.BaseDto;
import com.github.gcorporationcare.web.i18n.ParameterKey;
import com.github.gcorporationcare.web.service.BaseSlaveRegistrableService;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import springfox.documentation.annotations.ApiIgnore;

@Slf4j
public abstract class BaseSlaveRegistrableController<D extends BaseDto, E extends BaseEntity, ID extends Serializable, R extends BaseRepository<E, ID> & PagingAndSortingRepository<E, ID>, M_ID extends Serializable>
		extends BaseSlaveSearchableController<D, E, ID, R, M_ID> {

	private BaseSlaveRegistrableService<E, ID, R, M_ID> registrerService() {
		return (BaseSlaveRegistrableService<E, ID, R, M_ID>) service();
	}

	@ResponseBody
	@Transactional
	@ApiSimpleEndpoint
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(value = ParameterKey.EMPTY, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "create", notes = "Save/Update given entity")
	public D create(@PathVariable(ParameterKey.MASTER_PARAMETER) M_ID masterId,
			@Validated({ ValidationStep.Simple.class }) @RequestBody D dto,
			@ApiIgnore(ParameterKey.IGNORE_PARAMETER_REASON) FieldFilter<D> fieldFilter) {
		log.info("Saving entity {} with values {} for master {}", service().getEntityClass(), dto, masterId);
		E entity = mapFromDto(dto);
		E newEntity = registrerService().create(masterId, entity);
		return mapToDto(newEntity, fieldFilter);
	}

	@ResponseBody
	@Transactional
	@ApiSimpleEndpoint
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(value = "/multiple", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "create-multiple", notes = "Save/Update given entities")
	public Iterable<D> createMultiple(@PathVariable(ParameterKey.MASTER_PARAMETER) M_ID masterId,
			@RequestBody List<D> dtos, @ApiIgnore(ParameterKey.IGNORE_PARAMETER_REASON) FieldFilter<D> fieldFilter) {
		log.info("Saving entities {} with values {} for master {}", service().getEntityClass(), dtos, masterId);
		Iterable<E> entities = mapFromIterableDto(dtos);
		Iterable<E> newEntities = registrerService().createMultiple(masterId, entities);
		return mapToIterableDto(newEntities, fieldFilter);
	}

	@ResponseBody
	@Transactional
	@ApiIdentifiableEndpoint
	@ResponseStatus(HttpStatus.OK)
	@PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "update", notes = "Update given entity")
	public D update(@PathVariable(ParameterKey.MASTER_PARAMETER) M_ID masterId,
			@PathVariable(ParameterKey.ID_PARAMETER) ID id,
			@Validated({ ValidationStep.Complex.class }) @RequestBody D dto,
			@ApiIgnore(ParameterKey.IGNORE_PARAMETER_REASON) FieldFilter<D> fieldFilter) {
		log.info("Updating entity {} with values {} for master {}", service().getEntityClass(), dto, masterId);
		E entity = mapFromDto(dto);
		E newEntity = registrerService().update(masterId, id, entity);
		return mapToDto(newEntity, fieldFilter);
	}

	@ResponseBody
	@Transactional
	@ApiIdentifiableEndpoint
	@ResponseStatus(HttpStatus.OK)
	@PatchMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "patch", notes = "Update non-null fields of given entity")
	public D patch(@PathVariable(ParameterKey.MASTER_PARAMETER) M_ID masterId,
			@PathVariable(ParameterKey.ID_PARAMETER) ID id, @RequestBody D dto,
			@ApiIgnore(ParameterKey.IGNORE_PARAMETER_REASON) FieldFilter<D> fieldFilter) {
		log.info("Patching entity {} with values {} for master {}", service().getEntityClass(), dto, masterId);
		E entity = mapFromDto(dto);
		E newEntity = registrerService().patch(masterId, id, entity);
		return mapToDto(newEntity, fieldFilter);
	}

	@ResponseBody
	@Transactional
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "delete", notes = "Delete the given entity")
	public void delete(@PathVariable(ParameterKey.MASTER_PARAMETER) M_ID masterId,
			@PathVariable(ParameterKey.ID_PARAMETER) ID id) {
		log.info("Deleting entity {} with id {} for master {}", service().getEntityClass(), id, masterId);
		registrerService().delete(masterId, id);
	}

	@ResponseBody
	@Transactional
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping(value = "/multiple", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "delete-by-ids", notes = "Delete the entities with the given ids")
	public void deleteMultiple(@PathVariable(ParameterKey.MASTER_PARAMETER) M_ID masterId,
			@RequestParam(ParameterKey.IDS_PARAMETER) List<ID> ids) {
		log.info("Deleting entities with ids {} with values {} for master {}", service().getEntityClass(), ids,
				masterId);
		registrerService().deleteMultiple(masterId, ids);
	}
}
