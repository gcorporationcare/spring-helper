package com.gcorp.notest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gcorp.controller.BaseRegistrableController;
import com.gcorp.notest.entity.Person;
import com.gcorp.notest.repository.PersonRepository;
import com.gcorp.notest.service.PersonService;
import com.gcorp.service.BaseSearchableService;

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
