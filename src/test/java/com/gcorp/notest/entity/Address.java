package com.gcorp.notest.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.gcorp.annotation.DefaultField;
import com.gcorp.common.Utils;
import com.gcorp.constraint.AllOrNone;
import com.gcorp.domain.FieldFilter;
import com.gcorp.entity.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "address", indexes = { @Index(columnList = Address.PERSON_ID_COLUMN) })
@JsonFilter(FieldFilter.JSON_FILTER_NAME)
@AllOrNone(value = { Address.ZIP_COLUMN, Address.CITY_COLUMN, Address.STATE_COLUMN })
public class Address extends BaseEntity {

	protected static final String ID_COLUMN = "id";
	protected static final String ZIP_COLUMN = "zip";
	protected static final String NAME_COLUMN = "name";
	protected static final String CITY_COLUMN = "city";
	protected static final String STATE_COLUMN = "state";
	protected static final String ACTIVE_COLUMN = "active";
	protected static final String STREET_COLUMN = "street";
	protected static final String PERSON_ID_COLUMN = "person_id";

	private static final long serialVersionUID = 1L;
	// -------------------------------------------------
	@Id
	@DefaultField
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = Address.ID_COLUMN)
	private Long id;
	// -------------------------------------------------
	@Column(name = Address.NAME_COLUMN)
	private String name;
	// -------------------------------------------------
	@Column(name = Address.STREET_COLUMN)
	private String street;
	// -------------------------------------------------
	@Column(name = Address.ZIP_COLUMN)
	private String zip;
	// -------------------------------------------------
	@Column(name = Address.CITY_COLUMN)
	private String city;
	// -------------------------------------------------
	@Column(name = Address.STATE_COLUMN)
	private String state;
	// -------------------------------------------------
	@Column(name = Address.ACTIVE_COLUMN, nullable = false)
	private boolean active;
	// -------------------------------------------------
	@ManyToOne(optional = false)
	@JoinColumn(name = Address.PERSON_ID_COLUMN)
	private Person person;

	@Override
	public void format() {
		this.city = Utils.getProperNoun(this.city);
		this.state = Utils.getProperNoun(this.state);
		this.name = String.format("%s, %s (%s) %s", city, zip, state, street);
	}
}
