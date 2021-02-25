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

	protected String requestIdHeaderName() {
		return "Request-Id";
	}

	protected String languageParamName() {
		return "lang";
	}

	protected String searchFiltersParamName() {
		return "filters";
	}

	protected String fieldFiltersParamName() {
		return "fields";
	}

	protected String pageParamName() {
		return "page";
	}

	protected String sizeParamName() {
		return "size";
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(requestIdGenerator());
		registry.addInterceptor(localeChangeInterceptor());
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		PageableHandlerMethodArgumentResolver resolver = new PageableHandlerMethodArgumentResolver();
		resolver.setPageParameterName(pageParamName());
		resolver.setSizeParameterName(sizeParamName());
		resolver.setOneIndexedParameters(true);
		argumentResolvers.add(resolver);
		argumentResolvers.add(new SearchFiltersArgumentResolver(searchFiltersParamName()));
		argumentResolvers.add(new FieldFilterArgumentResolver(fieldFiltersParamName()));
	}

	@Bean
	public RequestIdGenerator requestIdGenerator() {
		return new RequestIdGenerator(requestIdHeaderName());
	}

	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
		interceptor.setParamName(languageParamName());
		return interceptor;
	}

	@Bean
	AuditorAware<String> auditorProvider() {
		return new AuditorAwareListener();
	}

	@Bean
	public LocaleResolver localeResolver() {
		SessionLocaleResolver resolver = new SessionLocaleResolver();
		resolver.setDefaultLocale(Locale.ENGLISH);
		return resolver;
	}

	@Bean
	public ResourceBundleMessageSource messageSource() {
		ResourceBundleMessageSource source = new ResourceBundleMessageSource();
		source.setBasenames("i18n/message");
		source.setUseCodeAsDefaultMessage(true);
		return source;
	}

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
