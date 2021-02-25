package com.gcorp.notest.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.gcorp.annotation.DefaultField;
import com.gcorp.domain.FieldFilter;
import com.gcorp.entity.BaseIdentifiedEntity;
import com.gcorp.field.Country;
import com.gcorp.field.FaxNumber;
import com.gcorp.field.HomeNumber;
import com.gcorp.field.MobileNumber;
import com.gcorp.field.MoneyCurrency;
import com.gcorp.field.converter.CountryConverter;
import com.gcorp.field.converter.FaxNumberConverter;
import com.gcorp.field.converter.HomeNumberConverter;
import com.gcorp.field.converter.MobileNumberConverter;
import com.gcorp.field.converter.MoneyCurrencyConverter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "office")
@JsonFilter(FieldFilter.JSON_FILTER_NAME)
public class Office extends BaseIdentifiedEntity {

	protected static final String NAME_COLUMN = "name";
	protected static final String COUNTRY_COLUMN = "country";
	protected static final String CURRENCY_COLUMN = "currency";
	protected static final String FAX_COLUMN = "fax";
	protected static final String HOME_COLUMN = "home";
	protected static final String MOBILE_COLUMN = "mobile";
	protected static final String EXPIRY_COLUMN = "expiry";
	protected static final String OPENING_COLUMN = "opening";

	private static final long serialVersionUID = 1L;
	// -------------------------------------------------
	@NotNull
	@NotEmpty
	@Column(name = NAME_COLUMN, nullable = false)
	private String name;
	// -------------------------------------------------
	@NotNull
	@DefaultField
	@Column(name = EXPIRY_COLUMN, nullable = false)
	protected LocalDate expiry;
	// -------------------------------------------------
	@NotNull
	@DefaultField
	@Column(name = OPENING_COLUMN, nullable = false)
	protected LocalTime opening;
	// -------------------------------------------------
	@NotNull
	@DefaultField
	@Convert(converter = CountryConverter.class)
	@Column(name = COUNTRY_COLUMN, nullable = false)
	protected Country country;
	// -------------------------------------------------
	@NotNull
	@DefaultField
	@Convert(converter = MoneyCurrencyConverter.class)
	@Column(name = CURRENCY_COLUMN, nullable = false)
	protected MoneyCurrency currency;
	// -------------------------------------------------
	@NotNull
	@DefaultField
	@Convert(converter = FaxNumberConverter.class)
	@Column(name = FAX_COLUMN, nullable = false)
	protected FaxNumber fax;
	// -------------------------------------------------
	@NotNull
	@DefaultField
	@Convert(converter = HomeNumberConverter.class)
	@Column(name = HOME_COLUMN, nullable = false)
	protected HomeNumber home;
	// -------------------------------------------------
	@NotNull
	@DefaultField
	@Convert(converter = MobileNumberConverter.class)
	@Column(name = MOBILE_COLUMN, nullable = false)
	protected MobileNumber mobile;

	@Override
	public void format() {
		// Nothing here
	}
}
