package com.github.gcorporationcare.data.field.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.github.gcorporationcare.data.field.MoneyCurrency;
import com.google.common.base.Strings;

@Converter
public class MoneyCurrencyConverter implements AttributeConverter<MoneyCurrency, String> {

	@Override
	public String convertToDatabaseColumn(MoneyCurrency currency) {
		if (currency == null || Strings.isNullOrEmpty(currency.getCode())) {
			return null;
		}
		return currency.getCode().toUpperCase();
	}

	@Override
	public MoneyCurrency convertToEntityAttribute(String dbCurrency) {
		if (Strings.isNullOrEmpty(dbCurrency)) {
			return null;
		}
		return MoneyCurrency.find(dbCurrency);
	}
}
