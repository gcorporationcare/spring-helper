package com.github.gcorporationcare.notest.entity;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.github.gcorporationcare.data.common.Utils;
import com.github.gcorporationcare.data.entity.BaseIdentifiedEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "person_tag")
public class PersonTag extends BaseIdentifiedEntity {

	protected static final String NAME_COLUMN = "name";
	protected static final String PERSON_ID_COLUMN = "person_id";

	private static final long serialVersionUID = 1L;
	// -------------------------------------------------
	@NotEmpty
	@Column(name = PersonTag.NAME_COLUMN, nullable = false)
	private String name;
	// -------------------------------------------------
	@Column(name = PersonTag.PERSON_ID_COLUMN, nullable = false)
	private Long personId;
	// -------------------------------------------------
	@NotNull
	@Embedded
	private PersonTagDescription description;

	@Override
	public void format() {
		// Nothing
		this.name = Utils.getProperNoun(this.name);
	}

	@Override
	protected Object[] embbeded() {
		return new Object[] { description };
	}
}
