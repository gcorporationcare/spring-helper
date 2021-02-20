package com.gcorp.config;

import java.time.LocalDateTime;
import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gcorp.i18n.I18nMessage;
import com.gcorp.listener.AuditorAwareListener;
import com.gcorp.serializer.LocalDateTimeDeserializer;
import com.gcorp.serializer.LocalDateTimeSerializer;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class ApiConfig {
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
	public I18nMessage apiMessage() {
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
