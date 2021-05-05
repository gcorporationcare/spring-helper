package com.github.gcorporationcare.notest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.gcorporationcare.notest.entity.Person;
import com.github.gcorporationcare.notest.repository.PersonRepository;
import com.github.gcorporationcare.web.service.BaseRegistrableService;

@Service
public class PersonService extends BaseRegistrableService<Person, Long, PersonRepository> {

	@Autowired
	PersonRepository personRepository;

	@Override
	public PersonRepository repository() {
		return personRepository;
	}

}
