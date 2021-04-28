package com.github.gcorporationcare.web.i18n;

import com.github.gcorporationcare.data.domain.SearchFilter.SearchFilterOperator;
import com.github.gcorporationcare.web.domain.FieldFilter;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ParameterKey {
	public static final String EMPTY = "";
	// -------------------------------------------------------------
	public static final String QUERY_PARAM_TYPE = "query";
	public static final String PATH_PARAM_TYPE = "path";

	public static final String INTEGER_DEFAULT_VALUE = "0";
	public static final String PAGE_PARAMETER_DEFAULT_VALUE = "1";
	public static final String SIZE_PARAMETER_DEFAULT_VALUE = "20";

	// -------------------------------------------------------------
	// Request parameters names
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
	// Parameters examples
	public static final String IGNORE_PARAMETER_REASON = "Ignored because Swagger UI shows wrong value";
	public static final String FIELDS_PARAMETER_EXAMPLE = "id,created,name";
	public static final String FILTERS_PARAMETER_EXAMPLE = "createdBy,!0";
	public static final String ID_PARAMETER_EXAMPLE = "12";
	public static final String IDS_PARAMETER_EXAMPLE = "1,2,5,16";
	public static final String PARENT_PARAMETER_EXAMPLE = "8";
	public static final String LANGUAGE_PARAMETER_EXAMPLE = "en";
	public static final String SORT_PARAMETER_EXAMPLE = "created,desc";

	// -------------------------------------------------------------
	// Parameters description
	public static final String ID_PARAMETER_DESCRIPTION = "Unique ID allowing to access a resource";
	public static final String PARENT_PARAMETER_DESCRIPTION = "Unique ID allowing to access a parent resource";
	public static final String PAGE_PARAMETER_DESCRIPTION = "Results page you want to retrieve (0..N)";
	public static final String LANGUAGE_PARAMETER_DESCRIPTION = "Language for searching data";
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

}
