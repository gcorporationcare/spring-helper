package com.gcorp.controller;

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

import com.gcorp.constraint.ValidationStep;
import com.gcorp.domain.FieldFilter;
import com.gcorp.entity.BaseEntity;
import com.gcorp.i18n.I18nMessage;
import com.gcorp.repository.BaseRepository;
import com.gcorp.service.BaseRegistrableService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseRegistrableController<E extends BaseEntity, ID extends Serializable, R extends BaseRepository<E, ID> & PagingAndSortingRepository<E, ID>>
		extends BaseSearchableController<E, ID, R> {

	private BaseRegistrableService<E, ID, R> registrerService() {
		return (BaseRegistrableService<E, ID, R>) service();
	}

	@ResponseBody
	@ApiOperation(value = "create", notes = "Save/Update given entity")
	@ApiImplicitParams({
			@ApiImplicitParam(name = I18nMessage.FIELDS_PARAMETER_NAME, dataType = I18nMessage.STRING_DATA_TYPE, paramType = I18nMessage.QUERY_PARAM_TYPE, value = I18nMessage.FIELDS_PARAMETER_DESCRIPTION) })
	@PostMapping(value = I18nMessage.EMPTY, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	@Transactional
	public E create(@Validated({ ValidationStep.Simple.class }) @RequestBody E entity, FieldFilter<E> fieldFilter) {
		log.info("Saving entity {} with values {}", service().getEntityClass(), entity);
		return registrerService().create(entity, fieldFilter);
	}

	@ResponseBody
	@ApiOperation(value = "create-multiple", notes = "Save/Update given entities")
	@ApiImplicitParams({
			@ApiImplicitParam(name = I18nMessage.FIELDS_PARAMETER_NAME, dataType = I18nMessage.STRING_DATA_TYPE, paramType = I18nMessage.QUERY_PARAM_TYPE, value = I18nMessage.FIELDS_PARAMETER_DESCRIPTION) })
	@PostMapping(value = "/multiple", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	@Transactional
	public final Iterable<E> createMultiple(@RequestBody List<E> entities, FieldFilter<E> fieldFilter) {
		log.info("Saving entities {} with values {}", service().getEntityClass(), entities);
		return registrerService().createMultiple(entities, fieldFilter);
	}

	@ResponseBody
	@ApiOperation(value = "update", notes = "Update given entity")
	@ApiImplicitParams({
			@ApiImplicitParam(name = I18nMessage.FIELDS_PARAMETER_NAME, dataType = I18nMessage.STRING_DATA_TYPE, paramType = I18nMessage.QUERY_PARAM_TYPE, value = I18nMessage.FIELDS_PARAMETER_DESCRIPTION) })
	@PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	@Transactional
	public final E update(@PathVariable(I18nMessage.ID_PARAMETER_NAME) ID id,
			@Validated({ ValidationStep.Complex.class }) @RequestBody E entity, FieldFilter<E> fieldFilter) {
		log.info("Updating entity {} with values {}", service().getEntityClass(), entity);
		return registrerService().update(id, entity, fieldFilter);
	}

	@ResponseBody
	@ApiOperation(value = "patch", notes = "Update non-null fields of given entity")
	@ApiImplicitParams({
			@ApiImplicitParam(name = I18nMessage.FIELDS_PARAMETER_NAME, dataType = I18nMessage.STRING_DATA_TYPE, paramType = I18nMessage.QUERY_PARAM_TYPE, value = I18nMessage.FIELDS_PARAMETER_DESCRIPTION) })
	@PatchMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	@Transactional
	public final E patch(@PathVariable(I18nMessage.ID_PARAMETER_NAME) ID id, @RequestBody E entity,
			FieldFilter<E> fieldFilter) {
		log.info("Patching entity {} with values {}", service().getEntityClass(), entity);
		return registrerService().patch(id, entity, fieldFilter);
	}

	@ResponseBody
	@ApiOperation(value = "delete", notes = "Delete the given entity")
	@DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Transactional
	public final void delete(@PathVariable(I18nMessage.ID_PARAMETER_NAME) ID id) {
		log.info("Deleting entity {} with id {}", service().getEntityClass(), id);
		registrerService().delete(id);
	}

	@ResponseBody
	@ApiOperation(value = "delete-by-ids", notes = "Delete the entities with the given ids")
	@DeleteMapping(value = "/multiple", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Transactional
	public final void deleteMultiple(@RequestParam(I18nMessage.IDS_PARAMETER_NAME) List<ID> ids) {
		log.info("Deleting entities with ids {} with values {}", service().getEntityClass(), ids);
		registrerService().deleteMultiple(ids);
	}
}
