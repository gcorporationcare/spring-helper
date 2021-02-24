package com.gcorp.field.converter;

import javax.persistence.Converter;

import com.gcorp.field.FaxNumber;
import com.gcorp.field.PhoneNumber;

@Converter
public class FaxNumberConverter extends PhoneNumberConverter<FaxNumber> {

	@Override
	protected FaxNumber fromPhoneNumber(PhoneNumber phoneNumber) {
		return new FaxNumber(phoneNumber.getAreaCode(), phoneNumber.getExtension(), phoneNumber.getPrefix(),
				phoneNumber.getSuffix());
	}
}
