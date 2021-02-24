package com.gcorp.notest.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.gcorp.annotation.DefaultField;
import com.gcorp.common.Utils;
import com.gcorp.constraint.InvalidWhen;
import com.gcorp.domain.FieldFilter;
import com.gcorp.entity.BaseIdentifiedEntity;
import com.gcorp.enumeration.Gender;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "person", uniqueConstraints = { @UniqueConstraint(columnNames = Person.EMAIL_COLUMN) }, indexes = {
		@Index(columnList = Person.EMAIL_COLUMN) })
@JsonFilter(FieldFilter.JSON_FILTER_NAME)
@InvalidWhen("email == null")
public class Person extends BaseIdentifiedEntity {

	protected static final String ID_COLUMN = "id";
	protected static final String NAME_COLUMN = "name";
	protected static final String EMAIL_COLUMN = "email";
	protected static final String GENDER_COLUMN = "gender";
	protected static final String LANGUAGE_COLUMN = "language";
	protected static final String PARENT_ID_COLUMN = "parent_id";

	private static final long serialVersionUID = 1L;
	// -------------------------------------------------
	@NotNull
	@NotEmpty
	@Column(name = Person.NAME_COLUMN, nullable = false)
	private String name;
	// -------------------------------------------------
	@Email
	@DefaultField
	@Column(name = Person.EMAIL_COLUMN, nullable = false)
	private String email;
	// -------------------------------------------------
	@Column(name = Person.LANGUAGE_COLUMN)
	private String language;
	// -------------------------------------------------
	@Column(name = Person.GENDER_COLUMN, nullable = false)
	private Gender gender;
	// -------------------------------------------------
	@ManyToOne
	@JoinColumn(name = Person.PARENT_ID_COLUMN)
	private Person parent;
	// -------------------------------------------------
	@Transient
	private int[] numbers;
	// -------------------------------------------------
	@Transient
	private Double[] floats;
	// -------------------------------------------------
	@Transient
	private String[] addresses;
	// -------------------------------------------------
	@Transient
	private Set<Person> booleans = new HashSet<>();

	@Override
	public void format() {
		// Nothing
		this.name = Utils.getProperNoun(this.name);
	}
}
