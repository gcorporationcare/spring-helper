package com.github.gcorporationcare.notest.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.gcorporationcare.notest.entity.PersonTag;
import com.github.gcorporationcare.notest.repository.PersonTagRepository;
import com.github.gcorporationcare.web.service.BaseChildRegistrableService;

import lombok.NonNull;

@Service
public class PersonTagService extends BaseChildRegistrableService<PersonTag, Long, PersonTagRepository, Long, Long> {

	@Autowired
	PersonTagRepository personTagRepository;

	@Override
	public PersonTagRepository repository() {
		return personTagRepository;
	}

	@Override
	public String getParentField() {
		return getParentIdField();
	}

	@Override
	public Optional<Long> findParent(@NonNull Long parentId) {
		return Optional.of(parentId);
	}

	@Override
	public String getParentIdField() {
		return "personId";
	}

}
