package com.github.gcorporationcare.notest.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.gcorporationcare.notest.entity.IdentityCard;
import com.github.gcorporationcare.notest.entity.Person;
import com.github.gcorporationcare.notest.repository.IdentityCardRepository;
import com.github.gcorporationcare.notest.repository.PersonRepository;
import com.github.gcorporationcare.web.service.BaseChildRegistrableService;

import lombok.NonNull;

@Service
public class IdentityCardService
		extends BaseChildRegistrableService<IdentityCard, Long, IdentityCardRepository, Long, Person> {
	@Autowired
	PersonRepository personRepository;
	@Autowired
	IdentityCardRepository identityCardRepository;

	@Override
	public IdentityCardRepository repository() {
		return identityCardRepository;
	}

	@Override
	public String getParentField() {
		return "person";
	}

	@Override
	public String getParentIdField() {
		return "person.id";
	}

	@Override
	public boolean isHardDelete() {
		// We do not want to delete identity card, we will disable instead
		// It will also be unlinked with owning person
		return false;
	}

	@Override
	public Optional<Person> findParent(@NonNull Long parentId) {
		return personRepository.findById(parentId);
	}
}
