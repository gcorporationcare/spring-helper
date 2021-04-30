package com.github.gcorporationcare.notest.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotEmpty;

import com.github.gcorporationcare.data.entity.BaseEmbedded;
import com.github.gcorporationcare.data.field.Country;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
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
