package com.gcorp.notest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gcorp.controller.BaseChildRegistrableController;
import com.gcorp.notest.entity.Address;
import com.gcorp.notest.entity.Person;
import com.gcorp.notest.repository.AddressRepository;
import com.gcorp.notest.repository.PersonRepository;
import com.gcorp.notest.service.AddressService;
import com.gcorp.service.BaseChildSearchableService;

@RestController
@RequestMapping("/persons/{parent}/addresses")
public class AddressController
		extends BaseChildRegistrableController<Address, Long, Person, Long, AddressRepository, PersonRepository> {

	@Autowired
	AddressService service;

	@Override
	public BaseChildSearchableService<Address, Long, Long, AddressRepository> service() {
		return service;
	}
}
