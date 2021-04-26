package com.github.gcorporationcare.notest.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.gcorporationcare.data.annotation.DefaultField;
import com.github.gcorporationcare.notest.enumeration.Gender;
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
public class PersonDto extends BaseIdentifiedDto {
	// -------------------------------------------------
	@NotEmpty
	private String name;
	// -------------------------------------------------
	@Email
	@DefaultField
	private String email;
	// -------------------------------------------------
	private String language;
	// -------------------------------------------------
	private Gender gender;
	// -------------------------------------------------
	private Integer age;
	// -------------------------------------------------
	private PersonDto parent;
}
