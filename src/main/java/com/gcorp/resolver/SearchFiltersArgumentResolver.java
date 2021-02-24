/**
 * 
 */
package com.gcorp.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.gcorp.domain.SearchFilters;
import com.google.common.base.Strings;

/**
 * @author AE.GNAMIAN
 *
 */
public class SearchFiltersArgumentResolver implements HandlerMethodArgumentResolver {

	private String argument;

	public SearchFiltersArgumentResolver(String argument) {
		this.argument = argument;
	}

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.getParameterType().equals(SearchFilters.class);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		String filters = webRequest.getParameter(this.argument);
		if (Strings.isNullOrEmpty(filters))
			return null;
		return SearchFilters.fromString(filters);
	}
}