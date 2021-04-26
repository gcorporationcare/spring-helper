package com.github.gcorporationcare.notest.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.gcorporationcare.ApiStarter;
import com.github.gcorporationcare.data.entity.BaseEmbedded;
import com.github.gcorporationcare.data.field.Country;
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
public class PersonTagAuthor extends BaseEmbedded {

	protected static final String FIRST_NAME_COLUMN = "first_name";
	protected static final String LAST_NAME_COLUMN = "last_name";
	protected static final String COUNTRY_COLUMN = "country";

	private static final long serialVersionUID = 1L;
	// -------------------------------------------------
	@NotEmpty
	@Column(name = PersonTagAuthor.FIRST_NAME_COLUMN, nullable = false)
	private String firstName;
	// -------------------------------------------------
	@NotEmpty
	@Column(name = PersonTagAuthor.LAST_NAME_COLUMN, nullable = false)
	private String lastName;
	// -------------------------------------------------
	@Column(name = PersonTagAuthor.COUNTRY_COLUMN)
	private Country country;

	@Override
	public void format() {
		// Nothing in there
	}
}
