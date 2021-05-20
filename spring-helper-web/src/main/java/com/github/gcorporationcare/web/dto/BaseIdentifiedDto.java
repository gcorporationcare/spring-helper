package com.github.gcorporationcare.web.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.github.gcorporationcare.data.annotation.DefaultField;
import com.github.gcorporationcare.data.i18n.I18nMessage;
import com.github.gcorporationcare.web.constraint.ValidationStep.OnPatch;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BaseIdentifiedDto extends BaseEntityDto {
	@DefaultField
	@Min(value = 1, message = I18nMessage.DataError.FIELD_REQUIRED)
	@NotNull(message = I18nMessage.DataError.FIELD_REQUIRED, groups = { OnPatch.class })
	private Long id;
}
