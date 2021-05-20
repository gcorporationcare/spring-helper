package com.github.gcorporationcare.data.field.converter;

import javax.persistence.Converter;

import com.github.gcorporationcare.data.field.BusinessNumber;
import com.github.gcorporationcare.data.field.PhoneNumber;

@Converter
public class BusinessNumberConverter extends PhoneNumberConverter<BusinessNumber> {

	@Override
	protected BusinessNumber fromPhoneNumber(PhoneNumber phoneNumber) {
		return new BusinessNumber(phoneNumber.getAreaCode(), phoneNumber.getExtension(), phoneNumber.getPrefix(),
				phoneNumber.getSuffix());
	}
}
