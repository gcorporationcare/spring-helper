package com.github.gcorporationcare.notest.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.gcorporationcare.ApiStarter;
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
@Table(name = "identity_card", indexes = { @Index(columnList = Address.PERSON_ID_COLUMN) })
@JsonIgnoreProperties(value = { ApiStarter.HIBERNATE_LAZY_INITIALIZER, ApiStarter.HANDLER }, ignoreUnknown = true)
public class IdentityCard extends BaseIdentifiedEntity {

	protected static final String NUMBER_COLUMN = "number";
	protected static final String PERSON_ID_COLUMN = "person_id";

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------
	@Column(name = IdentityCard.NUMBER_COLUMN)
	private String number;
	// -------------------------------------------------
	@ManyToOne
	@JoinColumn(name = IdentityCard.PERSON_ID_COLUMN)
	private Person person;

	@Override
	public void format() {
		this.number = this.number.toUpperCase().trim();
	}
}
