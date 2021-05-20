package com.github.gcorporationcare.data.field;

import com.github.gcorporationcare.data.enumeration.PhoneNumberType;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BusinessNumber extends PhoneNumber {

	private static final long serialVersionUID = 1L;

	public BusinessNumber(Country areaCode, String extension, String prefix, String suffix) {
		super(areaCode, extension, prefix, suffix, PhoneNumberType.BUSINESS);
	}

	public BusinessNumber(String areaCode, String extension, String prefix, String suffix) {
		super(areaCode, extension, prefix, suffix, PhoneNumberType.BUSINESS);
	}
}
