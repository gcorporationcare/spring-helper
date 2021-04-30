package com.github.gcorporationcare.notest.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.github.gcorporationcare.data.convention.SqlNamingConvention;
import com.github.gcorporationcare.data.entity.BaseTranslation;
import com.google.common.base.Strings;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "promotion_translation", uniqueConstraints = { @UniqueConstraint(columnNames = {
		SqlNamingConvention.Column.LANGUAGE, PromotionTranslation.SOURCE_ID_COLUMN }) })
public class PromotionTranslation extends BaseTranslation {
	protected static final String NAME_COLUMN = "name";
	protected static final String SOURCE_ID_COLUMN = "source_id";
	protected static final String DESCRIPTION_COLUMN = "description";

	private static final long serialVersionUID = 1L;
	// -------------------------------------------------
	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(name = PromotionTranslation.SOURCE_ID_COLUMN)
	protected Promotion source;
	// -------------------------------------------------
	@NotBlank
	@Column(name = PromotionTranslation.NAME_COLUMN, nullable = false)
	private String name;
	// -------------------------------------------------
	@NotBlank
	@Column(name = PromotionTranslation.DESCRIPTION_COLUMN, nullable = false)
	private String description;

	@Override
	public boolean translated() {
		return !Strings.isNullOrEmpty(description) && !Strings.isNullOrEmpty(name);
	}
}
