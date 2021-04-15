package com.gcorp.field;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.gcorp.enumeration.PhoneNumberType;
import com.gcorp.notest.common.RandomUtils;

class PhoneNumberTest {

	@Test
	void testConstructor() {
		try {
			PhoneNumber.newPhoneNumber(PhoneNumberType.BUSINESS, "", "prefix", "suffix", null);
		} catch (IllegalArgumentException e) {
			assertNotNull(e);
		}
		PhoneNumber phoneNumber = PhoneNumber.newPhoneNumber(PhoneNumberType.BUSINESS, "us", "33", "44", "55");
		assertNotNull(phoneNumber);
	}

	@Test
	void testFromString() {
		StringBuilder sb = new StringBuilder();
		List<PhoneNumber> phoneNumbers = new ArrayList<>();
		for (int index = 0; index < 100; index++) {
			String areaCode = "us";
			String prefix = String.format("%s%s", index / 2, index / 3);
			String suffix = String.format("%s%s%s%s", index * 4, index, index, index / 3);
			PhoneNumberType type = index % 2 == 0 ? PhoneNumberType.BUSINESS : null;
			PhoneNumber phoneNumber = PhoneNumber.newPhoneNumber(type, areaCode, prefix, suffix, prefix);
			if (type == null)
				assertEquals(PhoneNumberType.HOME, phoneNumber.getType());
			phoneNumbers.add(phoneNumber);
			sb.append(phoneNumber.toString());
			if (index != 99)
				sb.append(PhoneNumber.PHONE_NUMBERS_DELIMITER);
		}
		String phones = PhoneNumber.toString(phoneNumbers);
		assertEquals(sb.toString(), phones);
		List<PhoneNumber> fromString = PhoneNumber.fromString(phones);
		assertArrayEquals(phoneNumbers.toArray(), fromString.toArray());
		assertEquals(2, fromString.indexOf(fromString.get(2)));
	}

	@Test
	void testPhoneNumbers() {
		assertNotNull(RandomUtils.randomMobileNumber());
		assertNotNull(new MobileNumber("225", "11", "22", "33"));
		assertNotNull(RandomUtils.randomFaxNumber());
		assertNotNull(new FaxNumber("33", "44", "55", "66"));
		assertNotNull(RandomUtils.randomHomeNumber());
		assertNotNull(new HomeNumber("1", "77", "88", "99"));
	}

	@Test
	void testEquals() {
		MobileNumber mobileNumber = RandomUtils.randomMobileNumber();
		assertEquals(mobileNumber, mobileNumber);
		MobileNumber copyMobileNumber = new MobileNumber(mobileNumber.getAreaCode(), mobileNumber.getExtension(),
				mobileNumber.getPrefix(), mobileNumber.getSuffix());
		assertEquals(mobileNumber, copyMobileNumber);
		copyMobileNumber.setPrefix(null);
		MobileNumber anotherNumber = RandomUtils.randomMobileNumber();
		anotherNumber.setPrefix(mobileNumber.getPrefix() + "23");
		assertNotEquals(mobileNumber, copyMobileNumber);
		assertNotEquals(mobileNumber, anotherNumber);
		assertNotEquals(mobileNumber, RandomUtils.randomFaxNumber());
		assertNotEquals("Not a phone number", mobileNumber);
	}

	@Test
	void testHashCode() {
		MobileNumber mobileNumber1 = RandomUtils.randomMobileNumber();
		MobileNumber mobileNumber2 = RandomUtils.randomMobileNumber();
		mobileNumber2.setExtension(mobileNumber1.getExtension() + "4");
		assertNotEquals(mobileNumber1.hashCode(), mobileNumber2.hashCode());
	}

}
