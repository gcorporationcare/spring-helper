package com.gcorp.entity;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.gcorp.annotation.DefaultField;
import com.gcorp.annotation.NotCopyable;
import com.gcorp.common.Utils;
import com.gcorp.constraint.InvalidWhen;
import com.gcorp.convention.SqlNamingConvention;
import com.gcorp.domain.FieldFilter;
import com.gcorp.exception.ValidationException;
import com.gcorp.i18n.I18nMessage;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@MappedSuperclass
@NoArgsConstructor
@JsonFilter(FieldFilter.JSON_FILTER_NAME)
@ToString(callSuper = true, includeFieldNames = true)
public abstract class BaseTranslation extends BaseIdentifiedEntity {
	private static final long serialVersionUID = 1L;
	// -------------------------------------------------
	@NotCopyable
	@DefaultField
	@NotEmpty(message = I18nMessage.DataError.FIELD_REQUIRED)
	@NotNull(message = I18nMessage.DataError.FIELD_REQUIRED)
	@Column(name = SqlNamingConvention.Column.LANGUAGE, nullable = false, updatable = false)
	protected String language;

	public abstract boolean translated();

	@Override
	public void format() {
		List<String> languages = Arrays.asList(Locale.getISOLanguages());
		if (language != null && !languages.contains(language)) {
			throw new ValidationException(I18nMessage.DataError.FIELD_REQUIRED, Utils.generateViolations(
					InvalidWhen.class, I18nMessage.DataError.INCONSISTENT_VALUE_GIVEN, this, "language", null));
		}
	}
}
