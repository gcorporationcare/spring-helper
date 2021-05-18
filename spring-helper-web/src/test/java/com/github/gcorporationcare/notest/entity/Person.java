package com.github.gcorporationcare.notest.entity;

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

import com.github.gcorporationcare.data.annotation.DefaultField;
import com.github.gcorporationcare.data.common.Utils;
import com.github.gcorporationcare.data.constraint.InvalidExpression;
import com.github.gcorporationcare.data.constraint.InvalidWhen;
import com.github.gcorporationcare.data.entity.BaseIdentifiedEntity;
import com.github.gcorporationcare.notest.enumeration.Gender;

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
@InvalidWhen(@InvalidExpression(value = "email == null", field = "email"))
public class Person extends BaseIdentifiedEntity {

	protected static final String NAME_COLUMN = "name";
	protected static final String EMAIL_COLUMN = "email";
	protected static final String GENDER_COLUMN = "gender";
	protected static final String AGE_COLUMN = "age";
	protected static final String LANGUAGE_COLUMN = "language";
	protected static final String PARENT_ID_COLUMN = "parent_id";

	private static final long serialVersionUID = 1L;
	// -------------------------------------------------
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
	@Column(name = Person.AGE_COLUMN)
	private Integer age;
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
