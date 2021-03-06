package com.github.gcorporationcare.notest.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import com.github.gcorporationcare.data.entity.BaseEmbedded;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class PersonTagDescription extends BaseEmbedded {

	protected static final String TITLE_COLUMN = "title";
	protected static final String DESCRIPTION_COLUMN = "description";

	private static final long serialVersionUID = 1L;
	// -------------------------------------------------
	@NotEmpty
	@Column(name = PersonTagDescription.TITLE_COLUMN, nullable = false)
	private String title;
	// -------------------------------------------------
	@NotEmpty
	@Column(name = PersonTagDescription.DESCRIPTION_COLUMN, nullable = false)
	private String description;
	// -------------------------------------------------
	@Valid
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
