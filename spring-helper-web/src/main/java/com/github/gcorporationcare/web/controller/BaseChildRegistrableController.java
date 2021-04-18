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
import com.github.gcorporationcare.data.domain.FieldFilter;
import com.github.gcorporationcare.data.entity.BaseEntity;
import com.github.gcorporationcare.data.i18n.I18nMessage;
import com.github.gcorporationcare.data.repository.BaseRepository;
import com.github.gcorporationcare.web.service.BaseChildRegistrableService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import springfox.documentation.annotations.ApiIgnore;

@Slf4j
public abstract class BaseChildRegistrableController<E extends BaseEntity, ID extends Serializable, P_E extends BaseEntity, P_ID extends Serializable, R extends BaseRepository<E, ID> & PagingAndSortingRepository<E, ID>, P_R extends BaseRepository<P_E, P_ID> & PagingAndSortingRepository<P_E, P_ID>>
		extends BaseChildSearchableController<E, ID, P_ID, R> {

	@SuppressWarnings({ "unchecked" })
	private BaseChildRegistrableService<E, ID, P_E, P_ID, R, P_R> registrerService() {
		return (BaseChildRegistrableService<E, ID, P_E, P_ID, R, P_R>) service();
	}

	@ResponseBody
	@ApiOperation(value = "create", notes = "Save/Update given entity")
	@ApiImplicitParams({
			@ApiImplicitParam(name = I18nMessage.FIELDS_PARAMETER, dataTypeClass = String.class, paramType = I18nMessage.QUERY_PARAM_TYPE, value = I18nMessage.FIELDS_PARAMETER_DESCRIPTION) })
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	@Transactional
	public E create(@PathVariable(I18nMessage.PARENT_PARAMETER) P_ID parent,
			@Validated({ ValidationStep.Simple.class }) @RequestBody E child,
			@ApiIgnore(I18nMessage.IGNORE_PARAMETER) FieldFilter<E> fieldFilter) {
		log.info("Saving entity {} from parent {} with values {}", service().getEntityClass(), parent, child);
		return registrerService().create(parent, child, fieldFilter);
	}

	@ResponseBody
	@ApiOperation(value = "create-multiple", notes = "Save/Update given entities")
	@ApiImplicitParams({
			@ApiImplicitParam(name = I18nMessage.FIELDS_PARAMETER, dataTypeClass = String.class, paramType = I18nMessage.QUERY_PARAM_TYPE, value = I18nMessage.FIELDS_PARAMETER_DESCRIPTION) })
	@PostMapping(value = "/multiple", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	@Transactional
	public Iterable<E> createMultiple(@PathVariable(I18nMessage.PARENT_PARAMETER) P_ID parent,
			@RequestBody List<E> children, @ApiIgnore(I18nMessage.IGNORE_PARAMETER) FieldFilter<E> fieldFilter) {
		log.info("Saving entities {} from parent {} with values {}", service().getEntityClass(), parent, children);
		return registrerService().createMultiple(parent, children, fieldFilter);
	}

	@ResponseBody
	@ApiOperation(value = "update", notes = "Update given entity")
	@ApiImplicitParams({
			@ApiImplicitParam(name = I18nMessage.FIELDS_PARAMETER, dataTypeClass = String.class, paramType = I18nMessage.QUERY_PARAM_TYPE, value = I18nMessage.FIELDS_PARAMETER_DESCRIPTION) })
	@PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	@Transactional
	public E update(@PathVariable(I18nMessage.PARENT_PARAMETER) P_ID parent,
			@PathVariable(I18nMessage.ID_PARAMETER) ID childId,
			@Validated({ ValidationStep.Simple.class }) @RequestBody E child,
			@ApiIgnore(I18nMessage.IGNORE_PARAMETER) FieldFilter<E> fieldFilter) {
		log.info("Updating entity {} from parent {} with values {}", service().getEntityClass(), parent, child);
		return registrerService().update(parent, childId, child, fieldFilter);
	}

	@ResponseBody
	@ApiOperation(value = "patch", notes = "Update non-null fields of given entity")
	@ApiImplicitParams({
			@ApiImplicitParam(name = I18nMessage.FIELDS_PARAMETER, dataTypeClass = String.class, paramType = I18nMessage.QUERY_PARAM_TYPE, value = I18nMessage.FIELDS_PARAMETER_DESCRIPTION) })
	@PatchMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	@Transactional
	public E patch(@PathVariable(I18nMessage.PARENT_PARAMETER) P_ID parent,
			@PathVariable(I18nMessage.ID_PARAMETER) ID childId, @RequestBody E child,
			@ApiIgnore(I18nMessage.IGNORE_PARAMETER) FieldFilter<E> fieldFilter) {
		log.info("Patching entity {} from parent {} with values {}", service().getEntityClass(), parent, child);
		return registrerService().patch(parent, childId, child, fieldFilter);
	}

	@ResponseBody
	@ApiOperation(value = "delete", notes = "Delete the given entity")
	@DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Transactional
	public void delete(@PathVariable(I18nMessage.PARENT_PARAMETER) P_ID parent,
			@PathVariable(I18nMessage.ID_PARAMETER) ID childId) {
		log.info("Deleting entity {} from parent {} with id {}", service().getEntityClass(), parent, childId);
		registrerService().delete(parent, childId);
	}

	@ResponseBody
	@ApiOperation(value = "delete-by-ids", notes = "Delete the entities with the given ids")
	@DeleteMapping(value = "/multiple", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Transactional
	public void deleteMultiple(@PathVariable(I18nMessage.PARENT_PARAMETER) P_ID parent,
			@RequestParam(I18nMessage.IDS_PARAMETER) List<ID> ids) {
		log.info("Deleting entities from parent {} with ids {} with values {}", service().getEntityClass(), parent,
				ids);
		registrerService().deleteMultiple(parent, ids);
	}
}
