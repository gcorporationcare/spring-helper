package com.gcorp.notest.common;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;

import com.gcorp.enumeration.PhoneNumberType;
import com.gcorp.field.FaxNumber;
import com.gcorp.field.HomeNumber;
import com.gcorp.field.MobileNumber;
import com.gcorp.field.PhoneNumber;
import com.gcorp.notest.entity.Address;
import com.gcorp.notest.entity.Person;
import com.gcorp.notest.entity.Promotion;
import com.gcorp.notest.entity.PromotionTranslation;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Useful for generating random values of existing types
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RandomUtils {

	private static final int DEFAULT_MAX_SIZE = 100;

	public static String randomString(int count) {
		String value = RandomStringUtils.randomAlphabetic(count);
		return value != null ? value : UUID.randomUUID().toString().substring(count);
	}

	public static String randomString() {
		return randomString(Math.abs((new SecureRandom()).nextInt(DEFAULT_MAX_SIZE)));
	}

	public static String randomEmail(int count) {
		int length = count >= 6 ? (count - 4) / 2 : 3;
		return String.format("%s@%s.com", randomString(length), randomString(length));
	}

	public static String randomEmail() {
		return randomEmail(Math.abs((new SecureRandom()).nextInt(DEFAULT_MAX_SIZE)));
	}

	public static int randomInteger() {
		return (new SecureRandom()).nextInt();
	}

	public static int randomInteger(int max) {
		return (new SecureRandom()).nextInt(max);
	}

	public static double randomDouble() {
		return (new SecureRandom()).nextDouble();
	}

	public static boolean randomBoolean() {
		return (new SecureRandom()).nextBoolean();
	}

	public static Person randomPerson() {
		return new Person(RandomUtils.randomString(10), RandomUtils.randomEmail(), "en", new int[] { 1, 2 },
				new Double[] { 1.2, 2.3 }, new String[] { "add", "sub" }, new HashSet<>());
	}

	public static Address randomAddress(Person person) {
		Person owner = person == null ? randomPerson() : person;
		return new Address(null, "A given street", "12345", "Code-City", "Test-State", true, owner);
	}

	public static Promotion randomPromotion() {
		return new Promotion(null, "Super", "This is great", "en", LocalDateTime.now(),
				LocalDateTime.now().plusDays(30));
	}

	public static PromotionTranslation randomPromotionTranslation(Promotion promotion) {
		Promotion source = promotion == null ? randomPromotion() : promotion;
		PromotionTranslation translation = new PromotionTranslation(source, source.getName(), source.getDescription());
		translation.setLanguage(source.getLanguage());
		return translation;
	}

	public static PhoneNumber randomPhoneNumber(PhoneNumberType type) {
		PhoneNumberType theType = type != null ? type
				: PhoneNumberType.values()[randomInteger(PhoneNumberType.values().length)];
		return PhoneNumber.newPhoneNumber(theType, String.valueOf(1), String.valueOf(randomInteger(2)),
				String.valueOf(randomInteger(5)), String.valueOf(randomInteger(2)));
	}

	public static MobileNumber randomMobileNumber() {
		PhoneNumber phoneNumber = randomPhoneNumber(PhoneNumberType.MOBILE);
		return new MobileNumber(phoneNumber.getAreaCode(), phoneNumber.getPrefix(), phoneNumber.getSuffix(),
				phoneNumber.getExtension());
	}

	public static FaxNumber randomFaxNumber() {
		PhoneNumber phoneNumber = randomPhoneNumber(PhoneNumberType.FAX);
		return new FaxNumber(phoneNumber.getAreaCode(), phoneNumber.getPrefix(), phoneNumber.getSuffix(),
				phoneNumber.getExtension());
	}

	public static HomeNumber randomHomeNumber() {
		PhoneNumber phoneNumber = randomPhoneNumber(PhoneNumberType.HOME);
		return new HomeNumber(phoneNumber.getAreaCode(), phoneNumber.getPrefix(), phoneNumber.getSuffix(),
				phoneNumber.getExtension());
	}
}
