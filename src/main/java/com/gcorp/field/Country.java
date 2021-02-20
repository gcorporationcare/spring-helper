package com.gcorp.field;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import com.gcorp.common.Utils;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Country implements Serializable {

	private static final long serialVersionUID = 1L;

	private String code;
	private String name;
	private String displayName;
	private int dialCode;

	public static int getDialCode(String countryShortCode) {
		PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
		return phoneUtil.getCountryCodeForRegion(countryShortCode.toUpperCase());
	}

	public static Country find(@NonNull final String code) {
		Optional<Country> country = Utils.listCountries().stream().filter(c -> c.getCode().equalsIgnoreCase(code))
				.findFirst();
		return country.isPresent() ? country.get() : null;
	}

	public static List<Country> listCountries(String language) {
		Locale languageLocale = Utils.safeLocale(language);
		List<Country> countries = new ArrayList<>();
		String[] locales = Locale.getISOCountries();
		for (String code : locales) {
			Locale locale = new Locale(Utils.DEFAULT_LOCALE.getLanguage(), code);
			Country country = Country.builder().code(code).name(locale.getDisplayCountry())
					.displayName(locale.getDisplayCountry(languageLocale)).dialCode(getDialCode(locale.getCountry()))
					.build();
			countries.add(country);
		}
		Collections.sort(countries, (c1, c2) -> c1.displayName.compareToIgnoreCase(c2.displayName));
		return countries;
	}
}
