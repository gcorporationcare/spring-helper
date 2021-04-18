package com.github.gcorporationcare.data.config;

import java.time.LocalDateTime;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.gcorporationcare.data.i18n.I18nMessage;
import com.github.gcorporationcare.data.listener.AnonymousCurrentUserAuditor;
import com.github.gcorporationcare.data.listener.AuditorAwareListener;
import com.github.gcorporationcare.data.serializer.LocalDateTimeDeserializer;
import com.github.gcorporationcare.data.serializer.LocalDateTimeSerializer;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public abstract class DataConfig {

	protected AuditorAwareListener auditorAwareListener;

	/**
	 * Set the implementation of the current user auditor
	 * 
	 */
	@PostConstruct
	protected void configureUserAuditor() {
		auditorAwareListener = new AuditorAwareListener();
		auditorAwareListener.setCurrentUserAuditor(new AnonymousCurrentUserAuditor());
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

	/**
	 * The Bean that will be used to get current authenticated user when saving
	 * entities to database (created by, updated by).
	 * 
	 * @return an instance of the given bean
	 */
	@Bean
	public AuditorAware<String> auditorProvider() {
		return auditorAwareListener;
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
	public com.fasterxml.jackson.databind.Module javaTimeModule() {
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
