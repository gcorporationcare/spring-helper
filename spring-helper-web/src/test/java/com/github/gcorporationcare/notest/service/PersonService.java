package com.github.gcorporationcare.notest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.gcorporationcare.notest.entity.Person;
import com.github.gcorporationcare.notest.repository.PersonRepository;
import com.github.gcorporationcare.web.service.BaseRegistrableService;

import lombok.NonNull;

@Service
public class PersonService extends BaseRegistrableService<Person, Long, PersonRepository> {

	@Autowired
	PersonRepository personRepository;

	@Override
	public void checkForCreate(@NonNull Person entity) {
		// Test class : Can always create
	}

	@Override
	public void checkForUpdate(@NonNull Person entity, @NonNull Person savedEntity) {
		// Test class : Can always update
	}

	@Override
	public void checkForDelete(@NonNull Person entity) {
		// Test class : Can always delete
	}

	@Override
	public PersonRepository repository() {
		return personRepository;
	}

}
