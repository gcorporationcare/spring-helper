package com.github.gcorporationcare.web.dto;

import com.github.gcorporationcare.data.annotation.DefaultField;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BaseIdentifiedDto extends BaseEntityDto {
	@DefaultField
	private Long id;
}
