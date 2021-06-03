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

import com.github.gcorporationcare.data.entity.BaseEntity;
import com.github.gcorporationcare.data.repository.BaseRepository;
import com.github.gcorporationcare.web.annotation.ApiIdentifiableEndpoint;
import com.github.gcorporationcare.web.annotation.ApiSimpleEndpoint;
import com.github.gcorporationcare.web.common.ParameterKey;
import com.github.gcorporationcare.web.constraint.ValidationStep.OnCreate;
import com.github.gcorporationcare.web.constraint.ValidationStep.OnPatch;
import com.github.gcorporationcare.web.constraint.ValidationStep.OnUpdate;
import com.github.gcorporationcare.web.domain.FieldFilter;
import com.github.gcorporationcare.web.dto.BaseDto;
import com.github.gcorporationcare.web.service.BaseRegistrableService;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import springfox.documentation.annotations.ApiIgnore;

@Slf4j
public abstract class BaseRegistrableController<D extends BaseDto, E extends BaseEntity, I extends Serializable, R extends BaseRepository<E, I> & PagingAndSortingRepository<E, I>>
		extends BaseSearchableController<D, E, I, R> {

	private BaseRegistrableService<E, I, R> registrerService() {
		return (BaseRegistrableService<E, I, R>) service();
	}

	@ResponseBody
	@Transactional
	@ApiSimpleEndpoint
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(value = ParameterKey.EMPTY, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "create", notes = "Save/Update given entity")
	public D create(@Validated({ OnCreate.class }) @RequestBody D dto,
			@ApiIgnore(ParameterKey.IGNORE_PARAMETER_REASON) FieldFilter<D> fieldFilter) {
		log.info("Saving entity {} with values {}", service().getEntityClass(), dto);
		E entity = mapFromDto(dto);
		E newEntity = registrerService().create(entity);
		return mapToDto(newEntity, fieldFilter);
	}

	@ResponseBody
	@Transactional
	@ApiSimpleEndpoint
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(value = "/multiple", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "create-multiple", notes = "Save/Update given entities")
	public Iterable<D> createMultiple(@RequestBody List<D> dtos,
			@ApiIgnore(ParameterKey.IGNORE_PARAMETER_REASON) FieldFilter<D> fieldFilter) {
		dtos.stream().forEach(this::validateCreateMultiple);
		log.info("Saving entities {} with values {}", service().getEntityClass(), dtos);
		Iterable<E> entities = mapFromIterableDto(dtos);
		Iterable<E> newEntities = registrerService().createMultiple(entities);
		return mapToIterableDto(newEntities, fieldFilter);
	}

	@ResponseBody
	@Transactional
	@ApiIdentifiableEndpoint
	@ResponseStatus(HttpStatus.OK)
	@PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "update", notes = "Update given entity")
	public D update(@PathVariable(ParameterKey.ID_PARAMETER) I id, @Validated({ OnUpdate.class }) @RequestBody D dto,
			@ApiIgnore(ParameterKey.IGNORE_PARAMETER_REASON) FieldFilter<D> fieldFilter) {
		log.info("Updating entity {} with values {}", service().getEntityClass(), dto);
		E entity = mapFromDto(dto);
		E newEntity = registrerService().update(id, entity);
		return mapToDto(newEntity, fieldFilter);
	}

	@ResponseBody
	@Transactional
	@ApiIdentifiableEndpoint
	@ResponseStatus(HttpStatus.OK)
	@PatchMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "patch", notes = "Update non-null fields of given entity")
	public D patch(@PathVariable(ParameterKey.ID_PARAMETER) I id, @Validated({ OnPatch.class }) @RequestBody D dto,
			@ApiIgnore(ParameterKey.IGNORE_PARAMETER_REASON) FieldFilter<D> fieldFilter) {
		log.info("Patching entity {} with values {}", service().getEntityClass(), dto);
		E entity = mapFromDto(dto);
		E newEntity = registrerService().patch(id, entity);
		return mapToDto(newEntity, fieldFilter);
	}

	@ResponseBody
	@Transactional
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "delete", notes = "Delete the given entity")
	public void delete(@PathVariable(ParameterKey.ID_PARAMETER) I id) {
		log.info("Deleting entity {} with id {}", service().getEntityClass(), id);
		registrerService().delete(id);
	}

	@ResponseBody
	@Transactional
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping(value = "/multiple", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "delete-by-ids", notes = "Delete the entities with the given ids")
	public void deleteMultiple(@RequestParam(ParameterKey.IDS_PARAMETER) List<I> ids) {
		log.info("Deleting entities with ids {} with values {}", service().getEntityClass(), ids);
		registrerService().deleteMultiple(ids);
	}
}
