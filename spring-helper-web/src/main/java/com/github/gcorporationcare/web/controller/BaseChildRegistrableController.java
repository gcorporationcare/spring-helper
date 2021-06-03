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
import com.github.gcorporationcare.web.annotation.ApiSimpleEndpoint;
import com.github.gcorporationcare.web.common.ParameterKey;
import com.github.gcorporationcare.web.constraint.ValidationStep.OnCreate;
import com.github.gcorporationcare.web.constraint.ValidationStep.OnPatch;
import com.github.gcorporationcare.web.constraint.ValidationStep.OnUpdate;
import com.github.gcorporationcare.web.domain.FieldFilter;
import com.github.gcorporationcare.web.dto.BaseDto;
import com.github.gcorporationcare.web.service.BaseChildRegistrableService;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import springfox.documentation.annotations.ApiIgnore;

@Slf4j
public abstract class BaseChildRegistrableController<D extends BaseDto, E extends BaseEntity, I extends Serializable, R extends BaseRepository<E, I> & PagingAndSortingRepository<E, I>, P extends Serializable, V extends Serializable>
		extends BaseChildSearchableController<D, E, I, R, P> {

	@SuppressWarnings({ "unchecked" })
	private BaseChildRegistrableService<E, I, R, P, V> registrerService() {
		return (BaseChildRegistrableService<E, I, R, P, V>) service();
	}

	@ResponseBody
	@Transactional
	@ApiSimpleEndpoint
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "create", notes = "Save/Update given entity")
	public D create(@PathVariable(ParameterKey.PARENT_PARAMETER) P parent,
			@Validated({ OnCreate.class }) @RequestBody D dto,
			@ApiIgnore(ParameterKey.IGNORE_PARAMETER_REASON) FieldFilter<D> fieldFilter) {
		log.info("Saving entity {} from parent {} with values {}", service().getEntityClass(), parent, dto);
		E child = mapFromDto(dto);
		E newChild = registrerService().create(parent, child);
		return mapToDto(newChild, fieldFilter);
	}

	@ResponseBody
	@Transactional
	@ApiSimpleEndpoint
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(value = "/multiple", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "create-multiple", notes = "Save/Update given entities")
	public Iterable<D> createMultiple(@PathVariable(ParameterKey.PARENT_PARAMETER) P parent, @RequestBody List<D> dtos,
			@ApiIgnore(ParameterKey.IGNORE_PARAMETER_REASON) FieldFilter<D> fieldFilter) {
		dtos.stream().forEach(this::validateCreateMultiple);
		log.info("Saving entities {} from parent {} with values {}", service().getEntityClass(), parent, dtos);
		Iterable<E> children = mapFromIterableDto(dtos);
		Iterable<E> newEntities = registrerService().createMultiple(parent, children);
		return mapToIterableDto(newEntities, fieldFilter);
	}

	@ResponseBody
	@Transactional
	@ApiSimpleEndpoint
	@ResponseStatus(HttpStatus.OK)
	@PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "update", notes = "Update given entity")
	public D update(@PathVariable(ParameterKey.PARENT_PARAMETER) P parent,
			@PathVariable(ParameterKey.ID_PARAMETER) I childId, @Validated({ OnUpdate.class }) @RequestBody D dto,
			@ApiIgnore(ParameterKey.IGNORE_PARAMETER_REASON) FieldFilter<D> fieldFilter) {
		log.info("Updating entity {} from parent {} with values {}", service().getEntityClass(), parent, dto);
		E child = mapFromDto(dto);
		E newChild = registrerService().update(parent, childId, child);
		return mapToDto(newChild, fieldFilter);
	}

	@ResponseBody
	@Transactional
	@ApiSimpleEndpoint
	@ResponseStatus(HttpStatus.OK)
	@PatchMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "patch", notes = "Update non-null fields of given entity")
	public D patch(@PathVariable(ParameterKey.PARENT_PARAMETER) P parent,
			@PathVariable(ParameterKey.ID_PARAMETER) I childId, @Validated({ OnPatch.class }) @RequestBody D dto,
			@ApiIgnore(ParameterKey.IGNORE_PARAMETER_REASON) FieldFilter<D> fieldFilter) {
		log.info("Patching entity {} from parent {} with values {}", service().getEntityClass(), parent, dto);
		E child = mapFromDto(dto);
		E newChild = registrerService().patch(parent, childId, child);
		return mapToDto(newChild, fieldFilter);
	}

	@ResponseBody
	@Transactional
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "delete", notes = "Delete the given entity")
	public void delete(@PathVariable(ParameterKey.PARENT_PARAMETER) P parent,
			@PathVariable(ParameterKey.ID_PARAMETER) I childId) {
		log.info("Deleting entity {} from parent {} with id {}", service().getEntityClass(), parent, childId);
		registrerService().delete(parent, childId);
	}

	@ResponseBody
	@ApiOperation(value = "delete-by-ids", notes = "Delete the entities with the given ids")
	@DeleteMapping(value = "/multiple", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Transactional
	public void deleteMultiple(@PathVariable(ParameterKey.PARENT_PARAMETER) P parent,
			@RequestParam(ParameterKey.IDS_PARAMETER) List<I> ids) {
		log.info("Deleting entities from parent {} with ids {} with values {}", service().getEntityClass(), parent,
				ids);
		registrerService().deleteMultiple(parent, ids);
	}
}
