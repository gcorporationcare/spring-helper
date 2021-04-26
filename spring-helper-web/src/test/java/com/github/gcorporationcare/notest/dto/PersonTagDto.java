package com.github.gcorporationcare.notest.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.gcorporationcare.notest.entity.PersonTagDescription;
import com.github.gcorporationcare.web.domain.FieldFilter;
import com.github.gcorporationcare.web.dto.BaseIdentifiedDto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonFilter(FieldFilter.JSON_FILTER_NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonTagDto extends BaseIdentifiedDto {
	// -------------------------------------------------
	@NotEmpty
	private String name;
	// -------------------------------------------------
	private Integer personId;
	// -------------------------------------------------
	@NotNull
	private PersonTagDescription description;
}
