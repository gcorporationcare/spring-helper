package com.github.gcorporationcare.notest.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.gcorporationcare.ApiStarter;
import com.github.gcorporationcare.data.annotation.DefaultField;
import com.github.gcorporationcare.data.domain.FieldFilter;
import com.github.gcorporationcare.data.entity.BaseIdentifiedEntity;
import com.github.gcorporationcare.data.field.Country;
import com.github.gcorporationcare.data.field.FaxNumber;
import com.github.gcorporationcare.data.field.HomeNumber;
import com.github.gcorporationcare.data.field.MobileNumber;
import com.github.gcorporationcare.data.field.MoneyCurrency;
import com.github.gcorporationcare.data.field.converter.CountryConverter;
import com.github.gcorporationcare.data.field.converter.FaxNumberConverter;
import com.github.gcorporationcare.data.field.converter.HomeNumberConverter;
import com.github.gcorporationcare.data.field.converter.MobileNumberConverter;
import com.github.gcorporationcare.data.field.converter.MoneyCurrencyConverter;

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
@JsonIgnoreProperties(value = { ApiStarter.HIBERNATE_LAZY_INITIALIZER, ApiStarter.HANDLER }, ignoreUnknown = true)
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