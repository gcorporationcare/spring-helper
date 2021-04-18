package com.github.gcorporationcare.data.field.converter;

import javax.persistence.Converter;

import com.github.gcorporationcare.data.field.HomeNumber;
import com.github.gcorporationcare.data.field.PhoneNumber;

@Converter
public class HomeNumberConverter extends PhoneNumberConverter<HomeNumber> {

	@Override
	protected HomeNumber fromPhoneNumber(PhoneNumber phoneNumber) {
		return new HomeNumber(phoneNumber.getAreaCode(), phoneNumber.getExtension(), phoneNumber.getPrefix(),
				phoneNumber.getSuffix());
	}
}
