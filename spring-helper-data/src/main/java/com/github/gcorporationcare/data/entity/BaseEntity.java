package com.github.gcorporationcare.data.entity;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.github.gcorporationcare.data.annotation.Configure;
import com.github.gcorporationcare.data.annotation.NotCopyable;
import com.github.gcorporationcare.data.annotation.Translated;
import com.github.gcorporationcare.data.common.Utils;
import com.github.gcorporationcare.data.convention.SqlNamingConvention;
import com.github.gcorporationcare.data.domain.Constrainable;
import com.github.gcorporationcare.data.domain.Formattable;
import com.github.gcorporationcare.data.exception.StandardRuntimeException;
import com.github.gcorporationcare.data.exception.ValidationException;
import com.github.gcorporationcare.data.i18n.I18nMessage;
import com.github.gcorporationcare.data.listener.AuditorAwareListener;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Root entity for any object which can be stored in database
 */
@Slf4j
@ToString
@Configure
@MappedSuperclass
@NoArgsConstructor
@EntityListeners({ AuditingEntityListener.class, AuditorAwareListener.class })
public abstract class BaseEntity implements Serializable, Constrainable, Formattable {

	private static final long serialVersionUID = 1L;
	// -------------------------------------------------
	@Transient
	private static ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
	// -------------------------------------------------
	@Transient
	private static Validator validator = validatorFactory.getValidator();

	// -------------------------------------------------
	@Getter
	@CreatedDate
	@Column(name = SqlNamingConvention.Column.CREATED, nullable = false, updatable = false)
	private LocalDateTime created;
	// -------------------------------------------------
	@Getter
	@LastModifiedDate
	@Column(name = SqlNamingConvention.Column.UPDATED, nullable = false)
	private LocalDateTime updated;
	// -------------------------------------------------
	@Getter
	@CreatedBy
	@Column(name = SqlNamingConvention.Column.CREATED_BY, nullable = false, updatable = false)
	private String createdBy;
	// -------------------------------------------------
	@Getter
	@LastModifiedBy
	@Column(name = SqlNamingConvention.Column.UPDATED_BY, nullable = false)
	private String updatedBy;
	// ----------------------------------------------------
	/**
	 * This field is the master of everything
	 */
	@Transient
	private transient List<Configure> configurations;

	public final void validate() {
		BaseEntity.validateObject(this);
		Object[] embbededFields = embbeded();
		if (ArrayUtils.isEmpty(embbededFields)) {
			return;
		}
		Arrays.stream(embbededFields).filter(Objects::nonNull).forEach(BaseEntity::validateObject);
	}

	protected Object[] embbeded() {
		return new Object[0];
	}

	public static <T> void validateObject(T item) {
		Set<ConstraintViolation<Object>> violations = validator.validate(item);
		log.debug("Validation of {} ended with {}", item, violations);
		if (!violations.isEmpty()) {
			log.error("Validation error occured with {} on type {}", violations, item.getClass().getCanonicalName());
			throw new ValidationException(I18nMessage.DataError.UNIDENTIFIED_VALIDATION_ERROR_OCCURED, violations);
		}
	}

	/**
	 * Will be run before creating data in database
	 */
	@PrePersist
	public void create() {
		format();
		validate();
		log.debug("Creating row of class {} with data {}", getClass().getCanonicalName(), this);
	}

	/**
	 * Will be run before updating data in database
	 */
	@PreUpdate
	public void update() {
		format();
		validate();
		log.debug("Updating row of class {} with data {}", getClass().getCanonicalName(), this);
	}

	public void clearDates() {
		created = updated = null;
	}

	private List<Configure> retrieveConfiguration() {
		if (configurations != null)
			return configurations;
		configurations = Utils.listAnnotations(Configure.class, getClass(), BaseEntity.class);
		if (configurations == null || configurations.isEmpty()) {
			throw new StandardRuntimeException("Okay, the world will now end!!");
		}
		return configurations;
	}

	private Configure retrieveMainConfiguration() {
		return retrieveConfiguration().get(0);
	}

	/**
	 * Get the list of fields that can be translated
	 * 
	 * @return the fields that can be translated (will be found in linked
	 *         translation entity)
	 */
	public final Set<String> translatableFields() {
		String[] fields = Utils.getInheritedFields(getClass(), BaseEntity.class).stream()
				.filter(f -> f.isAnnotationPresent(Translated.class)).map(Field::getName).toArray(String[]::new);
		return new HashSet<>(Arrays.asList(fields));
	}

	/**
	 * Getting default sorting order for entity
	 * 
	 * @return the Sort object to apply by default when reading records from
	 *         database
	 */
	public final Sort defaultOrder() {
		Configure configure = retrieveMainConfiguration();
		List<Sort.Order> orders = Arrays.stream(configure.defaultSort())
				.map(s -> s.ascending() ? Sort.Order.asc(s.value()) : Sort.Order.desc(s.value()))
				.collect(Collectors.toList());
		return Sort.by(orders);
	}

	/**
	 * List of fields that cannot be copied
	 * 
	 * @return the Sort object to apply by default when reading records from
	 *         database
	 */
	public final Set<String> notCopyableField() {
		String[] fields = Utils.getInheritedFields(getClass(), BaseEntity.class).stream()
				.filter(f -> f.isAnnotationPresent(NotCopyable.class)).map(Field::getName).toArray(String[]::new);
		return new HashSet<>(Arrays.asList(fields));
	}

	/**
	 * Copy given object fields in current one
	 * 
	 * @param entity         the entity to copy values from
	 * @param excludedFields the field to ignore in read entity
	 */
	protected void copy(BaseEntity entity, String[] excludedFields) {
		if (entity == null)
			return;
		Set<String> ignoredProperties = notCopyableField();
		if (excludedFields != null && excludedFields.length > 0)
			ignoredProperties.addAll(new HashSet<>(Arrays.asList(excludedFields)));
		BeanUtils.copyProperties(entity, this, ignoredProperties.toArray(new String[] {}));
	}

	protected void copyListedProperties(BaseEntity entity, Set<String> properties) {
		String[] excludedProperties = Arrays.stream(BeanUtils.getPropertyDescriptors(this.getClass()))
				.map(PropertyDescriptor::getName).filter(name -> !properties.contains(String.valueOf(name)))
				.toArray(String[]::new);
		BeanUtils.copyProperties(entity, this, excludedProperties);
	}

	protected void copyAuditingProperties(BaseEntity entity) {
		this.created = entity.created;
		this.createdBy = entity.createdBy;
		this.updated = entity.updated;
		this.updatedBy = entity.updatedBy;
	}

	/**
	 * Copy current object fields to others
	 * 
	 * @param excludedFields the fields to exclude on current object when copying to
	 *                       others
	 * @param entities       the entities we want to copy the current instance value
	 *                       on
	 */
	protected void copyTo(String[] excludedFields, BaseEntity... entities) {
		if (entities == null)
			return;
		BaseEntity source = this;
		Arrays.stream(entities).filter(Objects::nonNull).forEach(e -> e.copy(source, excludedFields));
	}

	public void merge(BaseEntity entity, String[] excludedFields) {
		copy(entity, excludedFields);
	}
}
