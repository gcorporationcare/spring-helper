package com.github.gcorporationcare.web.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BaseEntityDto extends BaseDto {
	// -------------------------------------------------
	@JsonProperty(access = Access.READ_ONLY)
	private LocalDateTime created;
	// -------------------------------------------------
	@JsonProperty(access = Access.READ_ONLY)
	private LocalDateTime updated;
	// -------------------------------------------------
	@JsonProperty(access = Access.READ_ONLY)
	private String createdBy;
	// -------------------------------------------------
	@JsonProperty(access = Access.READ_ONLY)
	private String updatedBy;
	// ----------------------------------------------------
}
