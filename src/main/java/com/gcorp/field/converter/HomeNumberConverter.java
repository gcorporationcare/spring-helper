package com.gcorp.field.converter;

import javax.persistence.Converter;

import com.gcorp.field.HomeNumber;
import com.gcorp.field.PhoneNumber;

@Converter
public class HomeNumberConverter extends PhoneNumberConverter<HomeNumber> {

	@Override
	protected HomeNumber fromPhoneNumber(PhoneNumber phoneNumber) {
		return new HomeNumber(phoneNumber.getAreaCode(), phoneNumber.getExtension(), phoneNumber.getPrefix(),
				phoneNumber.getSuffix());
	}
}
