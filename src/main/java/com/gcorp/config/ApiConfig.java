package com.gcorp.config;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gcorp.common.RequestIdGenerator;
import com.gcorp.i18n.I18nMessage;
import com.gcorp.listener.AuditorAwareListener;
import com.gcorp.resolver.FieldFilterArgumentResolver;
import com.gcorp.resolver.SearchFiltersArgumentResolver;
import com.gcorp.serializer.LocalDateTimeDeserializer;
import com.gcorp.serializer.LocalDateTimeSerializer;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public abstract class ApiConfig implements WebMvcConfigurer {

	/**
	 * Each request receive a number that will be sent in response's header.
	 * Override this method in order to choose the header's name
	 * 
	 * @return the header that will be used
	 */
	protected String requestIdHeaderName() {
		return "Request-Id";
	}

	/**
	 * Location of the message file for i18n. Override this method in order to
	 * change location
	 * 
	 * @return the location of message file
	 */
	protected String i18nMessageSourceLocation() {
		return "i18n/message";
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
	 * The Bean that will be used to get current authenticated user when saving
	 * entities to database (created by, updated by).
	 * 
	 * @return an instance of the given bean
	 */
	@Bean
	AuditorAware<String> auditorProvider() {
		return new AuditorAwareListener();
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

	@Bean
	public ResourceBundleMessageSource messageSource() {
		ResourceBundleMessageSource source = new ResourceBundleMessageSource();
		source.setBasenames(i18nMessageSourceLocation());
		source.setUseCodeAsDefaultMessage(true);
		return source;
	}

	/**
	 * The Bean that will deal with translations
	 * 
	 * @return an instance of the given bean
	 */

	@Bean
	public I18nMessage i18nMessage() {
		I18nMessage i18nMessage = I18nMessage.getInstance();
		i18nMessage.setMessageSource(messageSource());
		return i18nMessage;
	}

	@Bean
	public Module javaTimeModule() {
		JavaTimeModule module = new JavaTimeModule();
		module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
		module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
		return module;
	}

	@Bean
	@Primary
	public ObjectMapper objectMapper() {
		return new ObjectMapper().setFilterProvider(new SimpleFilterProvider().setFailOnUnknownId(false))
				.setSerializationInclusion(JsonInclude.Include.NON_NULL).registerModule(javaTimeModule());
	}
}
