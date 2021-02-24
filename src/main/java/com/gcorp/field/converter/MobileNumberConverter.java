package com.gcorp.field.converter;

import javax.persistence.Converter;

import com.gcorp.field.MobileNumber;
import com.gcorp.field.PhoneNumber;

@Converter
public class MobileNumberConverter extends PhoneNumberConverter<MobileNumber> {

	@Override
	protected MobileNumber fromPhoneNumber(PhoneNumber phoneNumber) {
		return new MobileNumber(phoneNumber.getAreaCode(), phoneNumber.getExtension(), phoneNumber.getPrefix(),
				phoneNumber.getSuffix());
	}
}
