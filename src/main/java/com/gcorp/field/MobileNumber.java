package com.gcorp.field;

import com.gcorp.enumeration.PhoneNumberType;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MobileNumber extends PhoneNumber {

	private static final long serialVersionUID = 1L;

	public MobileNumber(Country areaCode, String extension, String prefix, String suffix) {
		super(areaCode, extension, prefix, suffix, PhoneNumberType.MOBILE);
	}
	
	public MobileNumber(String areaCode, String extension, String prefix, String suffix) {
		super(areaCode, extension, prefix, suffix, PhoneNumberType.MOBILE);
	}
}
