package com.gcorp.notest;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.gcorp.annotation.DefaultField;
import com.gcorp.constraint.InvalidWhen;
import com.gcorp.domain.FieldFilter;
import com.gcorp.entity.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonFilter(FieldFilter.JSON_FILTER_NAME)
@InvalidWhen("email == null || email.length < 3")
public class Person extends BaseEntity {
	private static final long serialVersionUID = 1L;
	private String name;
	@DefaultField
	private String email;
	private String language;
	private int[] numbers;
	private Double[] floats;
	private String[] addresses;
	private Set<Person> booleans = new HashSet<>();

	@Override
	public void format() {
		// Nothing
	}
}
