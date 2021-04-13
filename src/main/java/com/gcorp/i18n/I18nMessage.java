package com.gcorp.i18n;

import java.util.Locale;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;

import com.gcorp.domain.FieldFilter;
import com.gcorp.domain.SearchFilter.SearchFilterOperator;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class I18nMessage {

	@Setter
	private ResourceBundleMessageSource messageSource;
	private static I18nMessage i18nMessage;

	// -------------------------------------------------------------
	public static final String INTEGER_DATA_TYPE = "integer";
	public static final String QUERY_PARAM_TYPE = "query";
	public static final String STRING_DATA_TYPE = "string";
	public static final String BOOLEAN_DATA_TYPE = "boolean";

	// -------------------------------------------------------------
	public static final String LANGUAGE_PARAMETER = "lang";
	public static final String PARENT_PARAMETER = "parent";
	public static final String ID_PARAMETER = "id";
	public static final String IDS_PARAMETER = "ids";
	public static final String SORT_PARAMETER = "sort";
	public static final String PAGE_PARAMETER = "page";
	public static final String SIZE_PARAMETER = "size";
	public static final String FIELDS_PARAMETER = "fields";
	public static final String FILTERS_PARAMETER = "filters";

	// -------------------------------------------------------------
	public static final String PAGE_PARAMETER_DESCRIPTION = "Results page you want to retrieve (0..N)";
	public static final String LANGUAGE_PARAMETER_DESCRIPTION = "Language for searching data (default en)";
	public static final String SIZE_PARAMETER_DESCRIPTION = "Number of records per page";
	public static final String SORT_PARAMETER_DESCRIPTION = "Sorting criteria in the format: property(,asc|desc). "
			+ "Default sort order is ascending. " + "Multiple sort criteria are supported";
	public static final String FIELDS_PARAMETER_DESCRIPTION = "Fields displaying request in the format: field1,field2,field3. "
			+ "If not sent, only default fields will be displayed in response. " + "Use value \""
			+ FieldFilter.ALL_FIELDS + "\" for displaying all fields. ";

	public static final String FILTERS_PARAMETER_DESCRIPTION = "Filtering criteria in the format: property"
			+ SearchFilterOperator.FILTER_OPERATOR_DELIMITER + "operator"
			+ SearchFilterOperator.FILTER_OPERATOR_DELIMITER + "value(AND|OR)."
			+ "Default concatenation operator is AND=" + SearchFilterOperator.FILTER_AND + ", could use OR="
			+ SearchFilterOperator.FILTER_OR + " for linking multiple criteria. "
			+ "Multiple filtering criteria are supported: delimiter " + SearchFilterOperator.FILTERS_DELIMITER;

	public static final String EMPTY = "";

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
