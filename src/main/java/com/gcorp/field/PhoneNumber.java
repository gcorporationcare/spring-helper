package com.gcorp.field;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import com.gcorp.common.Utils;
import com.gcorp.enumeration.PhoneNumberType;
import com.gcorp.i18n.I18nMessage;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Deals with phone number representation
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PhoneNumber implements Serializable {

	private static final long serialVersionUID = 1L;
	// -------------------------------------------------
	public static final String PHONE_PART_DELIMITER = "-";
	// -------------------------------------------------
	public static final String PHONE_NUMBERS_DELIMITER = ":";
	// -------------------------------------------------
	@NotNull(message = I18nMessage.DataError.FIELD_REQUIRED)
	@NotEmpty(message = I18nMessage.DataError.FIELD_REQUIRED)
	private Country areaCode;
	// -------------------------------------------------
	@NotNull(message = I18nMessage.DataError.FIELD_REQUIRED)
	@NotEmpty(message = I18nMessage.DataError.FIELD_REQUIRED)
	private String extension;
	// -------------------------------------------------
	@NotNull(message = I18nMessage.DataError.FIELD_REQUIRED)
	@NotEmpty(message = I18nMessage.DataError.FIELD_REQUIRED)
	private String prefix;
	// -------------------------------------------------
	@NotNull(message = I18nMessage.DataError.FIELD_REQUIRED)
	@NotEmpty(message = I18nMessage.DataError.FIELD_REQUIRED)
	private String suffix;
	// -------------------------------------------------
	@Setter(AccessLevel.NONE)
	@NotNull(message = I18nMessage.DataError.FIELD_REQUIRED)
	private PhoneNumberType type;

	public PhoneNumber(String areaCode, String prefix, String suffix, String extension, PhoneNumberType type) {
		this(findAreaCode(areaCode), prefix, suffix, extension, type);
	}

	private static Country findAreaCode(String areaCode) {
		Optional<Country> country = Utils.listCountries().stream().filter(c -> c.getCode().equalsIgnoreCase(areaCode))
				.findFirst();
		return country.isPresent() ? country.get() : null;
	}

	public static PhoneNumber newPhoneNumber(PhoneNumberType type, String areaCode, String extension, String prefix,
			String suffix) {
		return newPhoneNumber(type, findAreaCode(areaCode), extension, prefix, suffix);
	}

	public static PhoneNumber newPhoneNumber(PhoneNumberType type, Country areaCode, String extension, String prefix,
			String suffix) {
		Assert.notNull(areaCode, "areaCode is required");
		Assert.hasText(extension, "extension is required");
		Assert.hasText(prefix, "prefix is required");
		Assert.hasText(suffix, "suffix is required");
		PhoneNumber phoneNumber = new PhoneNumber();
		phoneNumber.areaCode = areaCode;
		phoneNumber.extension = extension;
		phoneNumber.prefix = prefix;
		phoneNumber.suffix = suffix;
		phoneNumber.type = type == null ? PhoneNumberType.HOME : type;
		return phoneNumber;
	}

	public static List<PhoneNumber> fromString(String phones) {
		Assert.hasText(phones, "String is required");
		List<PhoneNumber> phoneNumbers = new ArrayList<>();
		String[] numbers = phones.split(PHONE_NUMBERS_DELIMITER);
		for (String number : numbers) {
			phoneNumbers.add(fromSingleString(number));
		}
		return phoneNumbers;
	}

	public static PhoneNumber fromSingleString(String number) {
		String[] parts = number.split(PHONE_PART_DELIMITER);
		Assert.isTrue(parts.length == 5, "Required 5 parts");
		return newPhoneNumber(PhoneNumberType.valueOf(parts[0]), findAreaCode(parts[1]), parts[2], parts[3], parts[4]);
	}

	public static String toString(List<PhoneNumber> phoneNumbers) {
		Assert.notNull(phoneNumbers, "Non null argument is required");
		StringBuilder sb = new StringBuilder();
		IntStream.range(0, phoneNumbers.size()).forEach(index -> {
			sb.append(phoneNumbers.get(index).toString());
			if (index != phoneNumbers.size() - 1)
				sb.append(PHONE_NUMBERS_DELIMITER);
		});
		return sb.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof PhoneNumber))
			return false;
		PhoneNumber that = (PhoneNumber) obj;
		return ObjectUtils.nullSafeEquals(areaCode, that.areaCode) && ObjectUtils.nullSafeEquals(prefix, that.prefix)
				&& ObjectUtils.nullSafeEquals(suffix, that.suffix) && ObjectUtils.nullSafeEquals(type, that.type)
				&& ObjectUtils.nullSafeEquals(extension, that.extension);
	}

	@Override
	public int hashCode() {
		int hashValue = 17;
		hashValue = 37 * hashValue + ObjectUtils.nullSafeHashCode(areaCode);
		hashValue = 37 * hashValue + ObjectUtils.nullSafeHashCode(extension);
		hashValue = 37 * hashValue + ObjectUtils.nullSafeHashCode(prefix);
		hashValue = 37 * hashValue + ObjectUtils.nullSafeHashCode(suffix);
		return hashValue;
	}

	@Override
	public String toString() {
		List<String> data = new ArrayList<>(
				Arrays.asList(type.toString(), areaCode.getCode(), extension, prefix, suffix));
		return String.join(PHONE_PART_DELIMITER, data);
	}
}
