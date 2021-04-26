package com.github.gcorporationcare.notest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.gcorporationcare.notest.entity.IdentityCard;
import com.github.gcorporationcare.notest.entity.Person;
import com.github.gcorporationcare.notest.repository.IdentityCardRepository;
import com.github.gcorporationcare.notest.repository.PersonRepository;
import com.github.gcorporationcare.web.service.BaseChildRegistrableService;

import lombok.NonNull;

@Service
public class IdentityCardService extends
		BaseChildRegistrableService<IdentityCard, Long, Person, Long, IdentityCardRepository, PersonRepository> {
	@Autowired
	PersonRepository personRepository;
	@Autowired
	IdentityCardRepository identityCardRepository;

	@Override
	public IdentityCardRepository repository() {
		return identityCardRepository;
	}

	@Override
	public PersonRepository parentRepository() {
		return personRepository;
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
	public void canCreate(@NonNull Long parentId, @NonNull IdentityCard child) {
		// Everybody can create

	}

	@Override
	public void canUpdate(@NonNull Long parentId, @NonNull IdentityCard child, @NonNull IdentityCard savedChild) {
		// Everybody can update
	}

	@Override
	public void canDelete(@NonNull Long parentId, @NonNull IdentityCard child) {
		// Everybody can delete
	}
}
