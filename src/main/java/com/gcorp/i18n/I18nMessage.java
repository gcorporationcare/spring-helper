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

	public static final String TYPE_PARAMETER = "type";
	public static final String LANGUAGE_PARAMETER = "language";
	public static final String ID_PARAMETER = "id";
	public static final String IDS_PARAMETER = "ids";
	public static final String PARENT_PARAMETER = "parent";
	public static final String PAGE_PARAMETER_NAME = "page";
	public static final String INTEGER_DATA_TYPE = "integer";
	public static final String QUERY_PARAM_TYPE = "query";
	public static final String STRING_DATA_TYPE = "string";
	public static final String BOOLEAN_DATA_TYPE = "boolean";
	public static final String PAGE_PARAMETER_DESCRIPTION = "Results page you want to retrieve (0..N)";
	public static final String LANGUAGE_PARAMETER_DESCRIPTION = "Language for searching data (default en)";
	public static final String SIZE_PARAMETER_NAME = "size";
	public static final String SIZE_PARAMETER_DESCRIPTION = "Number of records per page";
	public static final String SORT_PARAMETER_NAME = "sort";
	public static final String SORT_PARAMETER_DESCRIPTION = "Sorting criteria in the format: property(,asc|desc). "
			+ "Default sort order is ascending. " + "Multiple sort criteria are supported";
	public static final String FIELDS_PARAMETER_NAME = "fields";
	public static final String FIELDS_PARAMETER_DESCRIPTION = "Fields displaying request in the format: field1,field2,field3. "
			+ "If not sent, only default fields will be displayed in response. " + "Use value \""
			+ FieldFilter.ALL_FIELDS + "\" for displaying all fields. ";

	public static final String ENABLE_PARAMETER_NAME = "enable";
	public static final String ENABLE_PARAMETER_DESCRIPTION = "If false, will disable the correspunding record.";

	public static final String TEMPLATE_PARAMETER_NAME = "template";
	public static final String TEMPLATE_PARAMETER_DESCRIPTION = "Name of any existing template (will be inserted in layout).";

	public static final String FILE_PARAMETER_NAME = "file";
	public static final String FILE_PARAMETER_DESCRIPTION = "File containing the data to import into database; for CSV file, the delimiter must match the delimiter parameter's value.";
	public static final String DELIMITER_PARAMETER_NAME = "delimiter";
	public static final String DELIMITER_PARAMETER_DEFAULT = ";";
	public static final String DELIMITER_PARAMETER_DESCRIPTION = "CSV delimiter in the provided file.";
	public static final String OVERWRITE_ALLOWED_PARAMETER_NAME = "overwriteAllowed";
	public static final String OVERWRITE_ALLOWED_PARAMETER_DEFAULT = "false";
	public static final String OVERWRITE_ALLOWED_PARAMETER_DESCRIPTION = "If false, will not allow update of existing data.";

	public static final String FILTERS_PARAMETER_NAME = "filters";
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
		private static final String PREFIX = "dao.data_error.";
		public static final String URL_VALUE_EXPECTED = PREFIX + "url_value_expected";
		public static final String EMAIL_VALUE_EXPECTED = PREFIX + "email_value_expected";
		public static final String FIELD_REQUIRED = PREFIX + "field_required";
		public static final String POSITIVE_EXPECTED = PREFIX + "positive_expected";
		public static final String NEGATIVE_EXPECTED = PREFIX + "negative_expected";
		public static final String MULTIPLE_OR_NONE_EXPECTED = PREFIX + "multiple_or_none_expected";
		public static final String PAST_DATETIME_EXPECTED = PREFIX + "past_datetime_expected";
		public static final String INCONSISTENT_START_END = PREFIX + "inconsistent_start_end";
		public static final String MISMATCHING_VALUE_GIVEN = PREFIX + "mismatching_value_given";
		public static final String FUTURE_DATETIME_EXPECTED = PREFIX + "future_datetime_expected";
		public static final String INCONSISTENT_VALUE_GIVEN = PREFIX + "inconsistent_value_given";
		public static final String FORBIDDEN_VALUE_GIVEN = PREFIX + "forbidden_value_given";
		public static final String UNIDENTIFIED_VALIDATION_ERROR_OCCURED = PREFIX
				+ "unidentified_validation_error_occured";
		public static final String UNEXPECTED_ERROR_OCCURED = PREFIX + "unexpected_error_occured";
	}

	public abstract class RequestError extends I18nMessage {
		private static final String PREFIX = "dao.request_error.";
		public static final String NO_CURRENT_SESSION = PREFIX + "no_current_session";
		public static final String DATABASE_REQUEST_ERROR = PREFIX + "database_request_error";
		public static final String FORBIDDEN_ANONYMOUS_SESSION = PREFIX + "forbidden_anonymous_session";
		public static final String FORBIDDEN_OPERATION_SESSION = PREFIX + "forbidden_operation_session";
		public static final String INVALID_GIVEN_PARAMETERS = PREFIX + "invalid_given_parameters";
		public static final String FILE_IO_ISSUE = PREFIX + "file_io_issue";
		public static final String UNSUFFICIENT_RIGHTS = PREFIX + "unsifficient_rights";
		public static final String OBJECT_NOT_FOUND = PREFIX + "object_not_found";
		public static final String INVALID_USER_CREDENTIALS = PREFIX + "invalid_user_credentials";
		public static final String USER_NOT_YET_CONFIRMED = PREFIX + "user_not_yet_confirmed";
		public static final String USER_LOCKED = PREFIX + "user_locked";
		public static final String USER_ALREADY_EXISTS = PREFIX + "user_already_exists";
		public static final String OFFICE_ALREADY_EXISTS = PREFIX + "office_already_exists";
		public static final String UNEXPECTED_ROLE = PREFIX + "unexpected_role";
		public static final String USER_CREDENTIALS_MISMATCH = PREFIX + "user_credentials_mismatch";
		public static final String INVALID_SESSION = PREFIX + "invalid_session";
		public static final String EXPIRED_SESSION = PREFIX + "expired_session";
		public static final String FORBIDDEN_OPERATION = PREFIX + "forbidden_operation";
		public static final String IP_ADDRESS_BLOCKED = PREFIX + "ip_address_blocked";
		public static final String EMAIL_NOT_SENT = PREFIX + "email_not_sent";
		public static final String SEATS_CONSUMED = PREFIX + "seats_consumed";
		public static final String FREE_CONSUMED = PREFIX + "free_consumed";
	}
}
