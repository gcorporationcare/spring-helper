package com.gcorp.entity;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

import com.gcorp.domain.Constrainable;
import com.gcorp.domain.FieldFilterable;
import com.gcorp.domain.Formattable;

public abstract class BaseEmbedded implements Serializable, FieldFilterable, Constrainable, Formattable {

	private static final long serialVersionUID = 1L;
	// -------------------------------------------------

	@Override
	public final Set<String> defaultFields() {
		return FieldFilterable.defaultFields(getClass(), BaseEmbedded.class);
	}

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
