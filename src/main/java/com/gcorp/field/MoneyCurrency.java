package com.gcorp.field;

import java.io.Serializable;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.util.ObjectUtils;

import com.gcorp.common.Utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MoneyCurrency implements Serializable {

	private static final long serialVersionUID = 1L;

	private String code;
	private String symbol;
	private String name;
	private String displayName;

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof MoneyCurrency) || code == null)
			return false;
		MoneyCurrency that = (MoneyCurrency) obj;
		return that.code != null && code.equalsIgnoreCase(that.code);
	}

	@Override
	public int hashCode() {
		int hashValue = 17;
		return 37 * ObjectUtils.nullSafeHashCode(code) + hashValue;
	}

	public static MoneyCurrency find(@NonNull final String code) {
		Optional<MoneyCurrency> currency = Utils.listCurrencies().stream()
				.filter(c -> c.getCode().equalsIgnoreCase(code)).findFirst();
		return currency.isPresent() ? currency.get() : null;
	}

	public static List<MoneyCurrency> listCurrencies(String language) {
		Locale languageLocale = Utils.safeLocale(language);
		Locale defaultLocale = Utils.DEFAULT_LOCALE;
		List<MoneyCurrency> currencies = Currency.getAvailableCurrencies().stream()
				.map(c -> MoneyCurrency.builder().code(c.getCurrencyCode()).name(c.getDisplayName(defaultLocale))
						.symbol(c.getSymbol()).displayName(c.getDisplayName(languageLocale)).build())
				.collect(Collectors.toList());
		Collections.sort(currencies, (c1, c2) -> c1.displayName.compareToIgnoreCase(c2.displayName));
		return currencies;
	}
}
