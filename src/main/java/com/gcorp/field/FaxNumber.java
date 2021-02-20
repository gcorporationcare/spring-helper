package com.gcorp.field;

import com.gcorp.enumeration.PhoneNumberType;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FaxNumber extends PhoneNumber {

	private static final long serialVersionUID = 1L;

	public FaxNumber(Country areaCode, String extension, String prefix, String suffix) {
		super(areaCode, extension, prefix, suffix, PhoneNumberType.FAX);
	}

	public FaxNumber(String areaCode, String extension, String prefix, String suffix) {
		super(areaCode, extension, prefix, suffix, PhoneNumberType.FAX);
	}
}