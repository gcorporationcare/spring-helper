package com.gcorp.field;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.gcorp.enumeration.PhoneNumberType;
import com.gcorp.notest.common.RandomUtils;

public class PhoneNumberTest {

	@Test
	public void testConstructor() {
		try {
			PhoneNumber.newPhoneNumber(PhoneNumberType.BUSINESS, "", "prefix", "suffix", null);
		} catch (IllegalArgumentException e) {
			Assert.assertNotNull(e);
		}
		PhoneNumber phoneNumber = PhoneNumber.newPhoneNumber(PhoneNumberType.BUSINESS, "us", "33", "44", "55");
		Assert.assertNotNull(phoneNumber);
	}

	@Test
	public void testFromString() {
		StringBuilder sb = new StringBuilder();
		List<PhoneNumber> phoneNumbers = new ArrayList<>();
		for (int index = 0; index < 100; index++) {
			String areaCode = "us";
			String prefix = String.format("%s%s", index / 2, index / 3);
			String suffix = String.format("%s%s%s%s", index * 4, index, index, index / 3);
			PhoneNumberType type = index % 2 == 0 ? PhoneNumberType.BUSINESS : null;
			PhoneNumber phoneNumber = PhoneNumber.newPhoneNumber(type, areaCode, prefix, suffix, prefix);
			if (type == null)
				Assert.assertEquals(PhoneNumberType.HOME, phoneNumber.getType());
			phoneNumbers.add(phoneNumber);
			sb.append(phoneNumber.toString());
			if (index != 99)
				sb.append(PhoneNumber.PHONE_NUMBERS_DELIMITER);
		}
		String phones = PhoneNumber.toString(phoneNumbers);
		Assert.assertEquals(sb.toString(), phones);
		List<PhoneNumber> fromString = PhoneNumber.fromString(phones);
		Assert.assertArrayEquals(phoneNumbers.toArray(), fromString.toArray());
		Assert.assertEquals(2, fromString.indexOf(fromString.get(2)));
	}

	@Test
	public void testPhoneNumbers() {
		Assert.assertNotNull(RandomUtils.randomMobileNumber());
		Assert.assertNotNull(new MobileNumber("225", "11", "22", "33"));
		Assert.assertNotNull(RandomUtils.randomFaxNumber());
		Assert.assertNotNull(new FaxNumber("33", "44", "55", "66"));
		Assert.assertNotNull(RandomUtils.randomHomeNumber());
		Assert.assertNotNull(new HomeNumber("1", "77", "88", "99"));
	}

	@Test
	public void testEquals() {
		MobileNumber mobileNumber = RandomUtils.randomMobileNumber();
		Assert.assertEquals(mobileNumber, mobileNumber);
		MobileNumber copyMobileNumber = new MobileNumber(mobileNumber.getAreaCode(), mobileNumber.getExtension(),
				mobileNumber.getPrefix(), mobileNumber.getSuffix());
		Assert.assertEquals(mobileNumber, copyMobileNumber);
		copyMobileNumber.setPrefix(null);
		MobileNumber anotherNumber = RandomUtils.randomMobileNumber();
		anotherNumber.setPrefix(mobileNumber.getPrefix() + "23");
		Assert.assertNotEquals(mobileNumber, copyMobileNumber);
		Assert.assertNotEquals(mobileNumber, anotherNumber);
		Assert.assertNotEquals(mobileNumber, RandomUtils.randomFaxNumber());
		Assert.assertNotEquals(mobileNumber, "Not a phone number");
	}

	@Test
	public void testHashCode() {
		MobileNumber mobileNumber1 = RandomUtils.randomMobileNumber();
		MobileNumber mobileNumber2 = RandomUtils.randomMobileNumber();
		mobileNumber2.setExtension(mobileNumber1.getExtension() + "4");
		Assert.assertNotEquals(mobileNumber1.hashCode(), mobileNumber2.hashCode());
	}

}
