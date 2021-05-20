package com.github.gcorporationcare.web.constraint;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.junit.jupiter.api.Test;

import com.github.gcorporationcare.notest.common.DataProviderTestHelper;
import com.github.gcorporationcare.web.constraint.ValidationStep.OnCreate;
import com.github.gcorporationcare.web.constraint.ValidationStep.OnCreateAndUpdate;
import com.github.gcorporationcare.web.constraint.ValidationStep.OnPatch;
import com.github.gcorporationcare.web.constraint.ValidationStep.OnUpdate;

import lombok.AllArgsConstructor;
import lombok.Data;

class ValidationStepTest extends DataProviderTestHelper {
	static final String SIMPLE_VALUE = "We do not care";

	@Data
	@AllArgsConstructor
	class SimpleClass {
		@NotNull(groups = { OnPatch.class })
		private Long id;
		@NotEmpty(groups = { OnCreate.class })
		private String code;
		@NotEmpty(groups = { OnUpdate.class, OnCreate.class })
		private String name;
		@NotEmpty(groups = { OnCreateAndUpdate.class })
		private String description;
		@NotEmpty
		private String brand;
	}

	@Test
	void testOnPatch() {
		final SimpleClass simpleClass = new SimpleClass(1L, SIMPLE_VALUE, SIMPLE_VALUE, SIMPLE_VALUE, SIMPLE_VALUE);

		// 1- With valid object, we expect no violations
		validateConstraint(simpleClass, 0, null, OnPatch.class);

		// 2- With invalid OnCreate,
		// With invalid OnUpdate,
		// With invalid OnCreateAndUpdate
		// With invalid Default
		simpleClass.setCode(null);
		simpleClass.setName(null);
		simpleClass.setDescription(null);
		simpleClass.setBrand(null);
		// OnPatch only extends Default which is invalid
		validateConstraint(simpleClass, 1, null, OnPatch.class);

		// 3- With groups OnUpdate (extending OnPatch extending Default)
		simpleClass.setId(null);
		validateConstraint(simpleClass, 3, null, OnUpdate.class);

		// 4- With groups OnCreateAndUpdate (extending OnCreate and OnUpdate extending
		// OnPatch extending Default)
		validateConstraint(simpleClass, 5, null, OnCreateAndUpdate.class);

		// 5- With default group and invalid things
		validateConstraint(simpleClass, 1, null);
	}
}
