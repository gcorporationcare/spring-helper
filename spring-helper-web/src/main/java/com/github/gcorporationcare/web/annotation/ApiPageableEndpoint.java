package com.github.gcorporationcare.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.gcorporationcare.web.common.ParameterKey;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.Example;
import io.swagger.annotations.ExampleProperty;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ApiImplicitParams({
		@ApiImplicitParam(name = ParameterKey.FIELDS_PARAMETER, dataTypeClass = String.class, paramType = ParameterKey.QUERY_PARAM_TYPE, example = ParameterKey.FIELDS_PARAMETER_EXAMPLE, value = ParameterKey.FIELDS_PARAMETER_DESCRIPTION),
		@ApiImplicitParam(name = ParameterKey.FILTERS_PARAMETER, dataTypeClass = String.class, paramType = ParameterKey.QUERY_PARAM_TYPE, example = ParameterKey.FILTERS_PARAMETER_EXAMPLE, value = ParameterKey.FILTERS_PARAMETER_DESCRIPTION),
		@ApiImplicitParam(name = ParameterKey.PAGE_PARAMETER, dataTypeClass = Integer.class, defaultValue = ParameterKey.PAGE_PARAMETER_DEFAULT_VALUE, example = ParameterKey.PAGE_PARAMETER_DEFAULT_VALUE, paramType = ParameterKey.QUERY_PARAM_TYPE, value = ParameterKey.PAGE_PARAMETER_DESCRIPTION),
		@ApiImplicitParam(name = ParameterKey.SIZE_PARAMETER, dataTypeClass = Integer.class, defaultValue = ParameterKey.SIZE_PARAMETER_DEFAULT_VALUE, example = ParameterKey.SIZE_PARAMETER_DEFAULT_VALUE, paramType = ParameterKey.QUERY_PARAM_TYPE, value = ParameterKey.SIZE_PARAMETER_DESCRIPTION),
		@ApiImplicitParam(name = ParameterKey.SORT_PARAMETER, allowMultiple = true, dataTypeClass = String.class, paramType = ParameterKey.QUERY_PARAM_TYPE, examples = @Example(@ExampleProperty(ParameterKey.SORT_PARAMETER_EXAMPLE)), value = ParameterKey.SORT_PARAMETER_DESCRIPTION) })
public @interface ApiPageableEndpoint {

}
