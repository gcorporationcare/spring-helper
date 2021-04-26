package com.github.gcorporationcare.web.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.github.gcorporationcare.web.domain.FieldFilter;

public class FieldFilterArgumentResolver implements HandlerMethodArgumentResolver {

	private String argument;

	public FieldFilterArgumentResolver(String argument) {
		this.argument = argument;
	}

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.getParameterType().equals(FieldFilter.class);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		String filters = webRequest.getParameter(this.argument);
		return FieldFilter.fromString(filters);
	}
}