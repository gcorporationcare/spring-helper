package com.github.gcorporationcare.web.dto;

import javax.validation.constraints.NotNull;

import com.github.gcorporationcare.data.annotation.DefaultField;
import com.github.gcorporationcare.data.constraint.ValidationStep;
import com.github.gcorporationcare.data.i18n.I18nMessage;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BaseIdentifiedDto extends BaseEntityDto {
	@DefaultField
	@NotNull(message = I18nMessage.DataError.FIELD_REQUIRED, groups = { ValidationStep.OnPatch.class })
	private Long id;
}
