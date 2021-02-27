package com.gcorp.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.IntStream;

import javax.persistence.criteria.Path;

import org.springframework.util.ObjectUtils;

import com.bpodgursky.jbool_expressions.parsers.ExprParser;
import com.bpodgursky.jbool_expressions.rules.RuleSet;
import com.gcorp.common.Utils;
import com.gcorp.enumeration.PhoneNumberType;
import com.gcorp.field.Country;
import com.gcorp.field.FaxNumber;
import com.gcorp.field.HomeNumber;
import com.gcorp.field.MobileNumber;
import com.gcorp.field.MoneyCurrency;
import com.gcorp.field.PhoneNumber;
import com.google.common.collect.ObjectArrays;

import lombok.Getter;
import lombok.NonNull;

/**
 * Search filter object
 */
@Getter
public class SearchFilter implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final String INVALID_FIELD = "Criteria's field value cannot be empty";
	private static final String INVALID_OPERATOR = "Criteria's operator cannot be null";
	private static final String INVALID_VALUE = "Criteria's value cannot be null for these value, maybe you should use another operator";
	private static final String INVALID_CONSTRUCTOR = "This is not an adequate operator since it hasn't any default value";
	private static final String EXPRESSION_AND = "& ";
	private static final String EXPRESSION_OR = "| ";
	private static final String EXPRESSION_AND_OPERATOR = String.format("%s%s", SearchFilterOperator.FILTERS_DELIMITER,
			SearchFilterOperator.FILTER_AND);
	private static final String EXPRESSION_OR_OPERATOR = String.format("%s%s", SearchFilterOperator.FILTERS_DELIMITER,
			SearchFilterOperator.FILTER_OR);

	/**
	 * This field filter is an And/Or condition Use {@code true} for And
	 */
	protected boolean isAnd;
	/**
	 * Field on which we want to filter.<br>
	 * Use a dot (.) for embedded fields : person.company.phone
	 */
	protected String field;
	/**
	 * Operation that should be performed by this filter
	 */
	protected SearchFilterOperator operator;
	/**
	 * Value that will be used for comparisons (can be null or ignored depending on
	 * the filter)
	 */
	protected Serializable value;

	private SearchFilter(boolean isAnd, String field, SearchFilterOperator operator, boolean allowNullOperator,
			Serializable value, boolean allowNullValue) {
		if (field == null || field.isEmpty())
			throw new IllegalArgumentException(INVALID_FIELD);
		if (operator == null && !allowNullOperator)
			throw new IllegalArgumentException(INVALID_OPERATOR);
		if (value == null && !allowNullValue)
			throw new IllegalArgumentException(INVALID_VALUE);
		this.isAnd = isAnd;
		this.field = field;
		this.operator = operator;
		this.value = (operator != null && operator.isForce()) ? operator.getDefaultValue() : this.getTypedValue(value);
	}

	public SearchFilter(boolean isAnd, String field, SearchFilterOperator operator, Serializable value) {
		this(isAnd, field, operator, false, value, true);
	}

	public SearchFilter(boolean isAnd, String field, String sign, Serializable value) {
		this(isAnd, field, null, true, null, true);
		SearchFilterOperator op = SearchFilterOperator.get(sign);
		this.operator = op;
		this.value = op.isForce() ? op.getDefaultValue() : this.getTypedValue(value);
	}

	public SearchFilter(boolean isAnd, String field, SearchFilterOperator operator) {
		this(isAnd, field, operator, false, null, true);
		if (!operator.isForce())
			throw new IllegalArgumentException(INVALID_CONSTRUCTOR);
		this.value = operator.getDefaultValue();
	}

	public SearchFilter(boolean isAnd, String field, String sign) {
		this(isAnd, field, sign, null);
		if (!operator.isForce())
			throw new IllegalArgumentException(INVALID_CONSTRUCTOR);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Serializable getSafeValue(@NonNull Path<?> path, Serializable value) {
		if (value == null || path.getJavaType().isInstance(value)) {
			return value;
		}
		String stringValue = String.valueOf(value);
		if (path.getJavaType().isEnum()) {
			return Enum.valueOf((Class<Enum>) path.getJavaType(), stringValue);
		}
		if (isTemporalType(path)) {
			return getTemporalValue(path, stringValue);
		}
		if (isCustomFieldType(path)) {
			return getCustomFieldValue(path, stringValue);
		}
		return value;
	}

	public String getSafeStringValue(@NonNull Path<?> path, Serializable value) {
		if (value == null) {
			return null;
		}
		if (String.class.isInstance(value)) {
			return String.valueOf(value);
		}
		if (isTemporalType(path)) {
			return getTemporalStringValue(path, value);
		}
		if (isCustomFieldType(path)) {
			return getCustomFieldStringValue(path, value);
		}
		return value.toString();
	}

	/**
	 * Check if the target type expect a custom field value. Override this to add
	 * more tests if needed.
	 * 
	 * @param path the path containing the information on targeted type
	 * @return true if the targeted type is PhoneNumber (or any child),
	 *         MoneyCurrency or Country
	 */
	protected boolean isCustomFieldType(@NonNull Path<?> path) {
		boolean phoneType = PhoneNumber.class.isAssignableFrom(path.getJavaType());
		boolean countryOrCurrencyType = Country.class.equals(path.getJavaType())
				|| MoneyCurrency.class.equals(path.getJavaType());
		return phoneType || countryOrCurrencyType;
	}

	/**
	 * Get the custom field version of given string
	 * 
	 * @param path  containing the targeted type
	 * @param value the string to convert
	 * @return a PhoneNumber (or any child), MoneyCurrency or Country depending on
	 *         targeted type
	 */
	protected Serializable getCustomFieldValue(@NonNull Path<?> path, @NonNull String value) {
		if (MoneyCurrency.class.equals(path.getJavaType())) {
			return MoneyCurrency.find(value);
		}
		if (Country.class.equals(path.getJavaType())) {
			return Country.find(value);
		}
		if (PhoneNumber.class.isAssignableFrom(path.getJavaType())) {
			return getPhoneFieldValue(path, value);
		}
		return value;
	}

	private Serializable getPhoneFieldValue(@NonNull Path<?> path, @NonNull String value) {
		PhoneNumber phoneNumber = PhoneNumber.fromSingleString(value);
		if (FaxNumber.class.equals(path.getJavaType()) && PhoneNumberType.FAX.equals(phoneNumber.getType())) {
			return new FaxNumber(phoneNumber.getAreaCode(), phoneNumber.getExtension(), phoneNumber.getPrefix(),
					phoneNumber.getSuffix());
		}
		if (HomeNumber.class.equals(path.getJavaType()) && PhoneNumberType.HOME.equals(phoneNumber.getType())) {
			return new HomeNumber(phoneNumber.getAreaCode(), phoneNumber.getExtension(), phoneNumber.getPrefix(),
					phoneNumber.getSuffix());
		}
		if (MobileNumber.class.equals(path.getJavaType()) && PhoneNumberType.MOBILE.equals(phoneNumber.getType())) {
			return new MobileNumber(phoneNumber.getAreaCode(), phoneNumber.getExtension(), phoneNumber.getPrefix(),
					phoneNumber.getSuffix());
		}
		return phoneNumber;
	}

	protected String getCustomFieldStringValue(@NonNull Path<?> path, @NonNull Serializable value) {
		if (MoneyCurrency.class.equals(path.getJavaType())) {
			return ((MoneyCurrency) value).getCode();
		}
		if (Country.class.equals(path.getJavaType())) {
			return ((Country) value).getCode();
		}
		return value.toString();
	}

	/**
	 * Check if the target type expect a temporal value. Override this to add more
	 * tests if needed
	 * 
	 * @param path the path containing the information on targeted type
	 * @return true if the targeted type is LocalDateTime, LocalDate or LocalTime
	 */
	protected boolean isTemporalType(@NonNull Path<?> path) {
		return LocalDateTime.class.equals(path.getJavaType()) || LocalDate.class.equals(path.getJavaType())
				|| LocalTime.class.equals(path.getJavaType());
	}

	/**
	 * Get the temporal version of given string
	 * 
	 * @param path  containing the targeted type
	 * @param value the string to convert
	 * @return a LocalDateTime/LocalDate/LocalTime depending on targeted type
	 */
	protected Serializable getTemporalValue(@NonNull Path<?> path, @NonNull String value) {
		if (LocalDateTime.class.equals(path.getJavaType())) {
			return LocalDateTime.parse(value, DateTimeFormatter.ofPattern(Utils.API_DATETIME_FORMAT));
		}
		if (LocalDate.class.equals(path.getJavaType())) {
			return LocalDate.parse(value, DateTimeFormatter.ofPattern(Utils.API_DATE_FORMAT));
		}
		if (LocalTime.class.equals(path.getJavaType())) {
			return LocalTime.parse(value, DateTimeFormatter.ofPattern(Utils.API_TIME_FORMAT));
		}
		return value;
	}

	protected String getTemporalStringValue(@NonNull Path<?> path, @NonNull Serializable value) {
		if (LocalDateTime.class.equals(path.getJavaType())) {
			return ((LocalDateTime) value)
					.format(DateTimeFormatter.ofPattern(Utils.API_DATETIME_WITHOUT_OFFSET_FORMAT));
		}
		if (LocalDate.class.equals(path.getJavaType())) {
			return ((LocalDate) value).format(DateTimeFormatter.ofPattern(Utils.API_DATE_FORMAT));
		}
		if (LocalTime.class.equals(path.getJavaType())) {
			return ((LocalTime) value).format(DateTimeFormatter.ofPattern(Utils.API_TIME_FORMAT));
		}
		return value.toString();
	}

	private Serializable getTypedValue(Serializable value) {
		if (value == null)
			return null;
		String stringValue = value.toString().trim();
		if ("true".equalsIgnoreCase(stringValue) || "false".equalsIgnoreCase(stringValue))
			return Boolean.valueOf(stringValue.toLowerCase());
		return value;
	}

	/**
	 * Generate filter from a given String
	 * 
	 * @param filter the filter to read value from
	 * @return an instance of the filter
	 */
	public static SearchFilter fromString(String filter) {
		if (filter == null || filter.isEmpty() || !filter.contains(SearchFilterOperator.FILTER_OPERATOR_DELIMITER))
			throw new IllegalArgumentException(String.format(
					"Can't read search filters from null or empty strings or which fields aren't delimited by %s",
					SearchFilterOperator.FILTER_OPERATOR_DELIMITER));
		String[] fields = filter.replaceAll("\\" + SearchFilterOperator.FILTER_OR, "")
				.replace(SearchFilterOperator.FILTER_AND, "").split(SearchFilterOperator.FILTER_OPERATOR_DELIMITER);
		if (fields.length < 2)
			throw new IllegalArgumentException(
					"At least two fields are required on a filter: targed field and operator");
		boolean isAnd = !filter.startsWith(SearchFilterOperator.FILTER_OR)
				&& !filter.endsWith(SearchFilterOperator.FILTER_OR);
		return (fields.length == 2) ? new SearchFilter(isAnd, fields[0], fields[1])
				: new SearchFilter(isAnd, fields[0], fields[1], fields[2]);
	}

	public static SearchFilter[] filtersFromString(String filters) {
		if (filters == null || filters.isEmpty() || !filters.contains(SearchFilterOperator.FILTER_OPERATOR_DELIMITER))
			throw new IllegalArgumentException(String.format(
					"Can't read search filters from null or empty strings or which fields aren't delimited by %s",
					SearchFilterOperator.FILTER_OPERATOR_DELIMITER));
		String[] searchFilters = filters.split(SearchFilterOperator.FILTERS_DELIMITER);
		return Arrays.stream(searchFilters).map(SearchFilter::fromString).toArray(SearchFilter[]::new);
	}

	public static String asString(SearchFilter... filters) {
		if (filters == null)
			throw new NullPointerException("Cannot create string from null filters array");
		if (Utils.indexOfNull(Objects::isNull, filters) != -1)
			throw new NullPointerException("Cannot create string from array with one or multiple null filter(s)");
		return String.join(SearchFilterOperator.FILTERS_DELIMITER,
				Arrays.stream(filters).map(SearchFilter::toString).toArray(String[]::new));
	}

	public static SearchFilter[] compute(SearchFilter... filters) {
		Map<Character, SearchFilter> map = new HashMap<>();
		StringBuilder sb = new StringBuilder();
		IntStream.range(0, filters.length).forEach(i -> {
			char c = (char) (65 + i);
			map.put(c, filters[i]);
			if (sb.length() > 0) {
				sb.append(filters[i].isAnd ? EXPRESSION_AND : EXPRESSION_OR);
			}
			sb.append(c);
		});
		// (C | D | (A & B)) --> (C |D |(A &B)) --> (C;|D;|(A;-B))
		String expression = RuleSet.toDNF(ExprParser.parse(sb.toString())).toString();
		expression = expression.replace(EXPRESSION_AND, EXPRESSION_AND_OPERATOR)
				.replaceAll("\\" + EXPRESSION_OR, EXPRESSION_OR_OPERATOR).replaceAll("\\(", "").replaceAll("\\)", "")
				.replace(" ", "");
		for (Entry<Character, SearchFilter> e : map.entrySet()) {
			expression = expression.replaceAll(String.valueOf(e.getKey()), e.getValue().toSignLessString());
		}
		return filtersFromString(expression);
	}

	public static SearchFilter[] concat(boolean and, @NonNull SearchFilter[] filters1,
			@NonNull SearchFilter[] filters2) {
		if (filters1.length == 0)
			return filters2;
		if (filters2.length == 0 || Arrays.equals(filters1, filters2))
			return filters1;
		SearchFilter[] filters = ObjectArrays.concat(filters1, filters2, SearchFilter.class);
		Map<Character, SearchFilter> map = new HashMap<>();
		StringBuilder sb = new StringBuilder("(");
		IntStream.range(0, filters.length).forEach(i -> {
			char c = (char) (65 + i);
			map.put(c, filters[i]);
			if (i > 0 && i != filters1.length) {
				sb.append(filters[i].isAnd ? EXPRESSION_AND : EXPRESSION_OR);
			}
			sb.append(c);
			if (filters1.length == i + 1) {
				// Finishing first expression
				sb.append(String.format(")%s(", and ? EXPRESSION_AND : EXPRESSION_OR));
			}
		});
		sb.append(")"); // Closing last parenthesis
		String expression = RuleSet.toDNF(ExprParser.parse(sb.toString())).toString();
		expression = expression.replace(EXPRESSION_AND, EXPRESSION_AND_OPERATOR)
				.replaceAll("\\" + EXPRESSION_OR, EXPRESSION_OR_OPERATOR).replaceAll("\\(", "").replaceAll("\\)", "")
				.replace(" ", "");
		// 1- Avoid mistaking parsed value with alphabet letter used for
		// variables
		expression = convertedExpression(expression, map);
		return filtersFromString(expression);
	}

	private static String convertedExpression(final String expression, final Map<Character, SearchFilter> map) {
		final String safetyKeys = "#%s#";
		String converted = expression;
		for (Entry<Character, SearchFilter> e : map.entrySet()) {
			converted = converted.replaceAll(String.valueOf(e.getKey()), String.format(safetyKeys, e.getKey()));
		}
		for (Entry<Character, SearchFilter> e : map.entrySet()) {
			String safeKey = String.format(safetyKeys, e.getKey());
			converted = converted.replaceAll(safeKey, e.getValue().toSignLessString());
		}
		return converted;
	}

	public String toSignLessString() {
		String toString = String.format("%s%s%s", field, SearchFilterOperator.FILTER_OPERATOR_DELIMITER,
				operator.getSymbol());
		return (value == null) ? toString
				: String.format("%s%s%s", toString, SearchFilterOperator.FILTER_OPERATOR_DELIMITER, value.toString());
	}

	@Override
	public String toString() {
		String orAndOperator = isAnd ? SearchFilterOperator.FILTER_AND : SearchFilterOperator.FILTER_OR;
		return String.format("%s%s", orAndOperator, toSignLessString());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof SearchFilter))
			return false;
		SearchFilter other = (SearchFilter) obj;
		if (isAnd != other.isAnd)
			return false;
		boolean hasGoodValue = ObjectUtils.nullSafeEquals(value != null ? value.toString() : value,
				other.value != null ? other.value.toString() : other.value);
		return ObjectUtils.nullSafeEquals(field, other.field) && ObjectUtils.nullSafeEquals(operator, other.operator)
				&& hasGoodValue;
	}

	@Override
	public int hashCode() {
		int hashValue = 17;
		hashValue = 37 * hashValue + ObjectUtils.nullSafeHashCode(field);
		hashValue = 37 * hashValue + ObjectUtils.nullSafeHashCode(operator);
		hashValue = 37 * hashValue + ObjectUtils.nullSafeHashCode(value);
		return hashValue;
	}

	@Getter
	public enum SearchFilterOperator {
		/**
		 * Equality test
		 */
		IS_EQUAL("="),
		/**
		 * Equality test with case insensitive
		 */
		IS_EQUAL_CASE_INSENSITIVE(":="),
		/**
		 * Opposite of an equality test
		 */
		IS_NOT_EQUAL("<>"),
		/**
		 * Opposite of an case-insensitive equality test
		 */
		IS_NOT_EQUAL_CASE_INSENSITIVE(":<>"),
		/**
		 * Checks if value is greater than another one (suited for date too)
		 */
		IS_GREATER_THAN(">"),
		/**
		 * Checks if value is greater than or equal to another one (suited for date too)
		 */
		IS_GREATER_THAN_OR_EQUAL(">="),
		/**
		 * Checks if value is lesser than another one (suited for date too)
		 */
		IS_LESS_THAN("<"),
		/**
		 * Checks if value is lesser than or equal to another one (suited for date too)
		 */
		IS_LESS_THAN_OR_EQUAL("<="),
		/**
		 * Checks if a field has null value
		 */
		IS_NULL("0", true, null),
		/**
		 * Checks if a field has any non-null value
		 */
		IS_NOT_NULL("!0", true, null),
		/**
		 * Similarities comparisons: perfectly match SQL "like"
		 */
		IS_LIKE(":"),
		/**
		 * Similarities comparisons: perfectly match SQL "not like"
		 */
		IS_NOT_LIKE("!:"),
		/**
		 * Checks if field starts with a given value
		 */
		IS_STARTING_WITH("^"),
		/**
		 * Checks if field ends with a given value
		 */
		IS_ENDING_WITH("$"),
		/**
		 * Checks if field is in array of value
		 */
		IS_IN("<:>"),
		/**
		 * Checks if field is not in array of value
		 */
		IS_NOT_IN("!<:>");

		/**
		 * Used to separates multiple filters in single string
		 */
		public static final String FILTERS_DELIMITER = ";";
		/**
		 * Used to separates search filters field, operator and value on string filters
		 */
		public static final String FILTER_OPERATOR_DELIMITER = ",";
		/**
		 * Used to indicate a logical or
		 */
		public static final String FILTER_OR = "|";
		/**
		 * Used to indicate a logical and
		 */
		public static final String FILTER_AND = "-";

		private final String symbol;
		private final boolean force;
		private final Serializable defaultValue;

		SearchFilterOperator(String symbol, boolean force, Serializable defaultValue) {
			this.symbol = symbol;
			this.force = force;
			this.defaultValue = defaultValue;
		}

		SearchFilterOperator(String sign) {
			this(sign, false, null);
		}

		public static SearchFilterOperator get(String symbol) {
			List<SearchFilterOperator> list = Arrays.asList(SearchFilterOperator.values());
			for (SearchFilterOperator operator : list)
				if (operator.getSymbol().equalsIgnoreCase(symbol))
					return operator;
			throw new IllegalArgumentException(String.format("Unknown symbol %s", symbol));
		}
	}
}
