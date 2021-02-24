package com.gcorp.field.converter;

import java.util.List;

import javax.persistence.AttributeConverter;

import com.gcorp.field.PhoneNumber;
import com.google.common.base.Strings;

/**
 * Convert PhoneNumber property to database column
 */
public abstract class PhoneNumberConverter<T extends PhoneNumber> implements AttributeConverter<T, String> {

	@Override
	public String convertToDatabaseColumn(T phoneNumber) {
		if (phoneNumber == null) {
			return null;
		}
		return phoneNumber.toString();
	}

	@Override
	public T convertToEntityAttribute(String dbPhoneNumber) {
		if (Strings.isNullOrEmpty(dbPhoneNumber)) {
			return null;
		}
		List<PhoneNumber> phones = PhoneNumber.fromString(dbPhoneNumber);
		return phones != null && !phones.isEmpty() ? fromPhoneNumber(phones.get(0)) : null;
	}

	/**
	 * Converting parent object to child without casting...
	 */
	protected abstract T fromPhoneNumber(PhoneNumber phoneNumber);
}
