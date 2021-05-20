package com.github.gcorporationcare.data.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.github.gcorporationcare.data.annotation.Configure;
import com.github.gcorporationcare.data.annotation.DefaultField;
import com.github.gcorporationcare.data.annotation.FieldSort;
import com.github.gcorporationcare.data.annotation.NotCopyable;
import com.github.gcorporationcare.data.convention.SqlNamingConvention;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Simple entity with a numeric identifier
 */
@Setter
@Getter
@MappedSuperclass
@NoArgsConstructor
@ToString(callSuper = true, includeFieldNames = true)
@Configure(defaultSort = { @FieldSort(SqlNamingConvention.Column.ID) })
public abstract class BaseIdentifiedEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;
	// -------------------------------------------------
	@Id
	@NotCopyable
	@DefaultField
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = SqlNamingConvention.Column.ID)
	private Long id;

}
