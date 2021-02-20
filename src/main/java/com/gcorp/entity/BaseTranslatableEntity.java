package com.gcorp.entity;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.gcorp.annotation.NotCopyable;
import com.gcorp.common.Utils;
import com.gcorp.convention.SqlNamingConvention;
import com.gcorp.exception.StandardRuntimeException;
import com.google.common.base.Strings;

import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Represents entities that supports translation
 */
@MappedSuperclass
@NoArgsConstructor
@ToString(callSuper = true, includeFieldNames = true, exclude = { "translations" })
public abstract class BaseTranslatableEntity<T extends BaseTranslation> extends BaseEntity {
	private static final long serialVersionUID = 1L;
	// -------------------------------------------------
	@JsonIgnore
	@NotCopyable
	@JsonProperty(access = Access.READ_ONLY)
	@OneToMany(mappedBy = SqlNamingConvention.Column.SOURCE, cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	private Set<T> translations = new HashSet<>();

	public abstract String getCurrentLanguage();

	@JsonIgnore
	public Set<T> getTranslations() {
		if (translations == null) {
			translations = new HashSet<>();
		}
		return translations;
	}

	// Database translation management :: START
	@Override
	@PrePersist
	public void create() {
		T translation = getCurrentTranslation();
		translation.copyListedProperties(this, this.translatableFields());
		super.create();
	}

	@Override
	@PreUpdate
	public void update() {
		T translation = getCurrentTranslation();
		translation.copyListedProperties(this, this.translatableFields());
		super.update();
	}

	@PostLoad
	public void load() {
		T translation = getCurrentTranslation();
		if (translation.translated())
			this.copyListedProperties(translation, this.translatableFields());
		translation.copyListedProperties(this, this.translatableFields());
	}
	// Database translation management :: END

	@JsonIgnore
	public T getCurrentTranslation() {
		String language = getCurrentLanguage();
		T translation;
		if (Strings.isNullOrEmpty(language)) {
			language = Utils.DEFAULT_LOCALE.getLanguage();
		}
		translation = getTranslation(language);
		if (translation == null) {
			translation = newTranslation();
			getTranslations().add(translation);
			translation.setLanguage(language);
			Utils.setFieldValue("source", translation, BaseEntity.class, this);
		}
		return translation;
	}

	/**
	 * Create a new translation object from given entity type
	 */
	@SuppressWarnings("unchecked")
	public T newTranslation() {
		try {
			Type type = getClass().getGenericSuperclass();
			Class<T> clazz;
			if (type instanceof ParameterizedType) {
				ParameterizedType parameterizedType = (ParameterizedType) type;
				Type[] typeArguments = parameterizedType.getActualTypeArguments();
				clazz = (Class<T>) typeArguments[0];
				return clazz.newInstance();
			} else {
				return (T) BaseEntity.class.newInstance();
			}
		} catch (InstantiationException | IllegalAccessException e) {
			throw new StandardRuntimeException(e);
		}
	}

	/**
	 * Get the translation of a given language (if exists)
	 * 
	 * @param language the language code (i.e. en, fr...)
	 * @return {@code null} if no translation
	 */
	public T getTranslation(String language) {
		if (getTranslations().isEmpty() || Strings.isNullOrEmpty(language))
			return null;
		Optional<T> translation = getTranslations().stream()
				.filter(t -> t.getLanguage() != null && language.equalsIgnoreCase(t.getLanguage())).findFirst();
		return translation.isPresent() ? translation.get() : null;
	}
}
