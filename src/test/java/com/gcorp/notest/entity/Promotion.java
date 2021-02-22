package com.gcorp.notest.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.gcorp.annotation.DefaultField;
import com.gcorp.annotation.Translated;
import com.gcorp.common.Utils;
import com.gcorp.domain.FieldFilter;
import com.gcorp.entity.BaseTranslatableEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "promotion")
@JsonFilter(FieldFilter.JSON_FILTER_NAME)
public class Promotion extends BaseTranslatableEntity<PromotionTranslation> {

	protected static final String ID_COLUMN = "id";
	protected static final String NAME_COLUMN = "name";
	protected static final String START_COLUMN = "start";
	protected static final String END_COLUMN = "end";
	protected static final String LANGUAGE_COLUMN = "language";
	protected static final String DESCRIPTION_COLUMN = "description";

	private static final long serialVersionUID = 1L;
	// -------------------------------------------------
	@Id
	@DefaultField
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = Promotion.ID_COLUMN)
	private Long id;
	// -------------------------------------------------
	@NotNull
	@NotEmpty
	@Translated
	@Column(name = Promotion.NAME_COLUMN, nullable = false)
	private String name;
	// -------------------------------------------------
	@NotNull
	@NotEmpty
	@Translated
	@Column(name = Promotion.DESCRIPTION_COLUMN, nullable = false)
	private String description;
	// -------------------------------------------------
	@NotNull
	@NotEmpty
	@Column(name = Promotion.LANGUAGE_COLUMN, nullable = false)
	private String language;
	// -------------------------------------------------
	@Column(name = Promotion.START_COLUMN)
	private LocalDateTime start;
	// -------------------------------------------------
	@Column(name = Promotion.END_COLUMN)
	private LocalDateTime end;

	@Override
	public String getCurrentLanguage() {
		return language;
	}

	@Override
	public void format() {
		name = Utils.getProperNoun(name);
	}

}
