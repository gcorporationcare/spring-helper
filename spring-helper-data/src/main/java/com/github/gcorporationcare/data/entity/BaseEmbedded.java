package com.github.gcorporationcare.data.entity;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

import com.github.gcorporationcare.data.domain.Constrainable;
import com.github.gcorporationcare.data.domain.Formattable;

public abstract class BaseEmbedded implements Serializable, Constrainable, Formattable {

	private static final long serialVersionUID = 1L;
	// -------------------------------------------------

	@Override
	public void validate() {
		BaseEntity.validateObject(this);
		BaseEmbedded[] embbededFields = embedded();
		if (embbededFields == null) {
			return;
		}
		Arrays.stream(embbededFields).filter(Objects::nonNull).forEach(BaseEntity::validateObject);
	}

	protected BaseEmbedded[] embedded() {
		return new BaseEmbedded[0];
	}
}
