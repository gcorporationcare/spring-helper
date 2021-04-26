package com.github.gcorporationcare.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.gcorporationcare.web.i18n.ParameterKey;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ApiImplicitParams({
		@ApiImplicitParam(name = ParameterKey.PARENT_PARAMETER, dataTypeClass = Integer.class, paramType = ParameterKey.PATH_PARAM_TYPE, example = ParameterKey.PARENT_PARAMETER_EXAMPLE, value = ParameterKey.PARENT_PARAMETER_DESCRIPTION),
		@ApiImplicitParam(name = ParameterKey.FILTERS_PARAMETER, dataTypeClass = String.class, paramType = ParameterKey.QUERY_PARAM_TYPE, example = ParameterKey.FILTERS_PARAMETER_EXAMPLE, value = ParameterKey.FILTERS_PARAMETER_DESCRIPTION) })
public @interface ApiFilterableChildEndpoint {

}
