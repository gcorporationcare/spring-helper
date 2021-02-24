package com.gcorp.field.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.gcorp.field.Country;
import com.google.common.base.Strings;

@Converter
public class CountryConverter implements AttributeConverter<Country, String> {

	@Override
	public String convertToDatabaseColumn(Country country) {
		if (country == null || Strings.isNullOrEmpty(country.getCode())) {
			return null;
		}
		return country.getCode().toUpperCase();
	}

	@Override
	public Country convertToEntityAttribute(String dbCountry) {
		if (Strings.isNullOrEmpty(dbCountry)) {
			return null;
		}
		return Country.find(dbCountry);
	}
}
