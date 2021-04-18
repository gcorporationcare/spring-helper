package com.github.gcorporationcare.notest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.gcorporationcare.notest.entity.Person;
import com.github.gcorporationcare.notest.repository.PersonRepository;
import com.github.gcorporationcare.notest.service.PersonService;
import com.github.gcorporationcare.web.controller.BaseRegistrableController;
import com.github.gcorporationcare.web.service.BaseSearchableService;

@RestController
@RequestMapping("/persons")
public class PersonController extends BaseRegistrableController<Person, Long, PersonRepository> {

	@Autowired
	PersonService service;

	@Override
	public BaseSearchableService<Person, Long, PersonRepository> service() {
		return service;
	}
}
