package com.github.gcorporationcare.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.gcorporationcare.web.common.ParameterKey;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ApiImplicitParams({
		@ApiImplicitParam(name = ParameterKey.FIELDS_PARAMETER, dataTypeClass = String.class, paramType = ParameterKey.QUERY_PARAM_TYPE, example = ParameterKey.FIELDS_PARAMETER_EXAMPLE, value = ParameterKey.FIELDS_PARAMETER_DESCRIPTION),
		@ApiImplicitParam(name = ParameterKey.FILTERS_PARAMETER, dataTypeClass = String.class, paramType = ParameterKey.QUERY_PARAM_TYPE, example = ParameterKey.FILTERS_PARAMETER_EXAMPLE, value = ParameterKey.FILTERS_PARAMETER_DESCRIPTION) })
public @interface ApiFilterableEndpoint {

}
