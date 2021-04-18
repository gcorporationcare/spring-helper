package com.github.gcorporationcare.web.config;

import java.util.List;
import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.github.gcorporationcare.data.config.DataConfig;
import com.github.gcorporationcare.data.i18n.I18nMessage;
import com.github.gcorporationcare.web.interceptor.RequestIdGenerator;
import com.github.gcorporationcare.web.listener.SecuredUserAuditor;
import com.github.gcorporationcare.web.resolver.FieldFilterArgumentResolver;
import com.github.gcorporationcare.web.resolver.SearchFiltersArgumentResolver;

@Configuration
public abstract class ApiConfig extends DataConfig implements WebMvcConfigurer {

	/**
	 * Each request receive a number that will be sent in response's header.
	 * Override this method in order to choose the header's name
	 * 
	 * @return the header that will be used
	 */
	protected String requestIdHeaderName() {
		return "Request-Id";
	}

	@Override
	protected void configureUserAuditor() {
		super.configureUserAuditor();
		auditorAwareListener.setCurrentUserAuditor(new SecuredUserAuditor());
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(requestIdGenerator());
		registry.addInterceptor(localeChangeInterceptor());
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		PageableHandlerMethodArgumentResolver resolver = new PageableHandlerMethodArgumentResolver();
		resolver.setPageParameterName(I18nMessage.PAGE_PARAMETER);
		resolver.setSizeParameterName(I18nMessage.SIZE_PARAMETER);
		resolver.setOneIndexedParameters(true);
		argumentResolvers.add(resolver);
		argumentResolvers.add(new SearchFiltersArgumentResolver(I18nMessage.FILTERS_PARAMETER));
		argumentResolvers.add(new FieldFilterArgumentResolver(I18nMessage.FIELDS_PARAMETER));
	}

	/**
	 * The Bean that will add an ID to each received request.
	 * 
	 * @return an instance of the given bean
	 */
	@Bean
	public RequestIdGenerator requestIdGenerator() {
		return new RequestIdGenerator(requestIdHeaderName());
	}

	/**
	 * The Bean that will decide the method used to detect locale change
	 * 
	 * @return an instance of the given bean
	 */
	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
		interceptor.setParamName(I18nMessage.LANGUAGE_PARAMETER);
		return interceptor;
	}

	/**
	 * The type of Locale resolver
	 * 
	 * @return an instance of the given bean
	 */

	@Bean
	public LocaleResolver localeResolver() {
		SessionLocaleResolver resolver = new SessionLocaleResolver();
		resolver.setDefaultLocale(Locale.ENGLISH);
		return resolver;
	}
}
