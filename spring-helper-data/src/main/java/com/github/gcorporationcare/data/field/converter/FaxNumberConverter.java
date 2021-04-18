package com.github.gcorporationcare.data.field.converter;

import javax.persistence.Converter;

import com.github.gcorporationcare.data.field.FaxNumber;
import com.github.gcorporationcare.data.field.PhoneNumber;

@Converter
public class FaxNumberConverter extends PhoneNumberConverter<FaxNumber> {

	@Override
	protected FaxNumber fromPhoneNumber(PhoneNumber phoneNumber) {
		return new FaxNumber(phoneNumber.getAreaCode(), phoneNumber.getExtension(), phoneNumber.getPrefix(),
				phoneNumber.getSuffix());
	}
}
