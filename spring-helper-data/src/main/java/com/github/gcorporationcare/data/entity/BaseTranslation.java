package com.github.gcorporationcare.data.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import com.github.gcorporationcare.data.annotation.DefaultField;
import com.github.gcorporationcare.data.annotation.NotCopyable;
import com.github.gcorporationcare.data.constraint.LanguageCode;
import com.github.gcorporationcare.data.convention.SqlNamingConvention;
import com.github.gcorporationcare.data.i18n.I18nMessage;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@MappedSuperclass
@NoArgsConstructor
@ToString(callSuper = true, includeFieldNames = true)
public abstract class BaseTranslation extends BaseIdentifiedEntity {

	private static final long serialVersionUID = 1L;
	// -------------------------------------------------
	@NotCopyable
	@DefaultField
	@LanguageCode(message = I18nMessage.DataError.LANGUAGE_CODE_EXPECTED)
	@Column(name = SqlNamingConvention.Column.LANGUAGE, nullable = false, updatable = false)
	protected String language;

	public abstract boolean translated();

	@Override
	public void format() {
		language = language.toLowerCase();
	}
}
