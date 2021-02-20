package com.gcorp.field;

import com.gcorp.enumeration.PhoneNumberType;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HomeNumber extends PhoneNumber {

	private static final long serialVersionUID = 1L;

	public HomeNumber(Country areaCode, String extension, String prefix, String suffix) {
		super(areaCode, extension, prefix, suffix, PhoneNumberType.HOME);
	}

	public HomeNumber(String areaCode, String extension, String prefix, String suffix) {
		super(areaCode, extension, prefix, suffix, PhoneNumberType.HOME);
	}
}
