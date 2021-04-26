package com.github.gcorporationcare.data.i18n;

import java.util.Locale;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class I18nMessage {

	@Setter
	private ResourceBundleMessageSource messageSource;
	private static I18nMessage i18nMessage;

	public static I18nMessage getInstance() {
		if (i18nMessage == null) {
			i18nMessage = new I18nMessage();
		}
		return i18nMessage;
	}

	public String getMessage(String key) {
		Object[] objects = null;
		return getMessage(key, null, objects);
	}

	public String getMessage(String key, String language) {
		return getMessage(key, new Locale(language));
	}

	public String getMessage(String key, Locale locale) {
		Object[] objects = null;
		return getMessage(key, locale, objects);
	}

	public String getMessage(String key, Object... objects) {
		return getMessage(key, null, objects);
	}

	public String getMessage(String key, Locale locale, Object... objects) {
		Locale selectedLocale = locale == null ? LocaleContextHolder.getLocale() : locale;
		return messageSource.getMessage(key, objects, selectedLocale);
	}

	public abstract class DataError extends I18nMessage {
		private static final String PREFIX = "data_error.";
		public static final String EMAIL_VALUE_EXPECTED = PREFIX + "email_value_expected";
		public static final String FIELD_REQUIRED = PREFIX + "field_required";
		public static final String MULTIPLE_OR_NONE_EXPECTED = PREFIX + "multiple_or_none_expected";
		public static final String LANGUAGE_CODE_EXPECTED = PREFIX + "language_code_expected";
		public static final String INCONSISTENT_VALUE_GIVEN = PREFIX + "inconsistent_value_given";
		public static final String FORBIDDEN_VALUE_GIVEN = PREFIX + "forbidden_value_given";
		public static final String UNIDENTIFIED_VALIDATION_ERROR_OCCURED = PREFIX
				+ "unidentified_validation_error_occured";
		public static final String UNEXPECTED_ERROR_OCCURED = PREFIX + "unexpected_error_occured";
	}

	public abstract class RequestError extends I18nMessage {
		private static final String PREFIX = "request_error.";
		public static final String INVALID_GIVEN_PARAMETERS = PREFIX + "invalid_given_parameters";
		public static final String OBJECT_NOT_FOUND = PREFIX + "object_not_found";
		public static final String FORBIDDEN_OPERATION = PREFIX + "forbidden_operation";
	}
}
