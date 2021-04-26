package com.github.gcorporationcare.notest.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.gcorporationcare.ApiStarter;
import com.github.gcorporationcare.data.constraint.ValidationStep;
import com.github.gcorporationcare.data.entity.BaseEmbedded;
import com.github.gcorporationcare.web.domain.FieldFilter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@JsonFilter(FieldFilter.JSON_FILTER_NAME)
@JsonIgnoreProperties(value = { ApiStarter.HIBERNATE_LAZY_INITIALIZER, ApiStarter.HANDLER }, ignoreUnknown = true)
public class PersonTagDescription extends BaseEmbedded {

	protected static final String TITLE_COLUMN = "title";
	protected static final String DESCRIPTION_COLUMN = "description";

	private static final long serialVersionUID = 1L;
	// -------------------------------------------------
	@NotEmpty
	@Column(name = PersonTagDescription.TITLE_COLUMN, nullable = false)
	private String title;
	// -------------------------------------------------
	@NotEmpty(groups = { ValidationStep.Complex.class })
	@Column(name = PersonTagDescription.DESCRIPTION_COLUMN, nullable = false)
	private String description;
	// -------------------------------------------------
	@NotNull
	@Embedded
	private PersonTagAuthor author;

	@Override
	public void format() {
		title = title.toUpperCase().trim();
	}

	@Override
	protected BaseEmbedded[] embedded() {
		return new BaseEmbedded[] { author };
	}

}
