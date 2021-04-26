package com.github.gcorporationcare.notest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.gcorporationcare.notest.entity.PersonTag;
import com.github.gcorporationcare.notest.repository.PersonTagRepository;
import com.github.gcorporationcare.web.service.BaseSlaveRegistrableService;

import lombok.NonNull;

@Service
public class PersonTagService extends BaseSlaveRegistrableService<PersonTag, Long, PersonTagRepository, Long> {

	@Autowired
	PersonTagRepository personTagRepository;

	@Override
	public void canCreate(@NonNull Long masterId, @NonNull PersonTag entity) {
		// Test class : Can always create
	}

	@Override
	public void canUpdate(@NonNull Long masterId, @NonNull PersonTag entity, @NonNull PersonTag savedEntity) {
		// Test class : Can always update
	}

	@Override
	public void canDelete(@NonNull Long masterId, @NonNull PersonTag entity) {
		// Test class : Can always delete
	}

	@Override
	public PersonTagRepository repository() {
		return personTagRepository;
	}

	@Override
	public String getMasterField() {
		return "personId";
	}
}
