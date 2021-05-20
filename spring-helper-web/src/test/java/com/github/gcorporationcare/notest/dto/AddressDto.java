package com.github.gcorporationcare.notest.dto;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
public class AddressDto extends BaseIdentifiedDto {
	private String name;
	// -------------------------------------------------
	private String street;
	// -------------------------------------------------
	private String zip;
	// -------------------------------------------------
	private String city;
	// -------------------------------------------------
	private String state;
	// -------------------------------------------------
	private boolean active;
	// -------------------------------------------------
	@Valid
	private PersonDto person;
}
