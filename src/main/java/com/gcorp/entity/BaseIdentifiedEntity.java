package com.gcorp.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.gcorp.annotation.Configure;
import com.gcorp.annotation.DefaultField;
import com.gcorp.annotation.FieldSort;
import com.gcorp.annotation.NotCopyable;
import com.gcorp.convention.SqlNamingConvention;
import com.gcorp.domain.FieldFilter;

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
@JsonFilter(FieldFilter.JSON_FILTER_NAME)
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
