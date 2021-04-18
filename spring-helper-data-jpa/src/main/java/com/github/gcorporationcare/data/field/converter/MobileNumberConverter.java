package com.github.gcorporationcare.data.field.converter;

import javax.persistence.Converter;

import com.github.gcorporationcare.data.field.MobileNumber;
import com.github.gcorporationcare.data.field.PhoneNumber;

@Converter
public class MobileNumberConverter extends PhoneNumberConverter<MobileNumber> {

	@Override
	protected MobileNumber fromPhoneNumber(PhoneNumber phoneNumber) {
		return new MobileNumber(phoneNumber.getAreaCode(), phoneNumber.getExtension(), phoneNumber.getPrefix(),
				phoneNumber.getSuffix());
	}
}
