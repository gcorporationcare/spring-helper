package com.github.gcorporationcare.notest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.gcorporationcare.notest.entity.Address;
import com.github.gcorporationcare.notest.entity.Person;
import com.github.gcorporationcare.notest.repository.AddressRepository;
import com.github.gcorporationcare.notest.repository.PersonRepository;
import com.github.gcorporationcare.notest.service.AddressService;
import com.github.gcorporationcare.web.controller.BaseChildRegistrableController;
import com.github.gcorporationcare.web.service.BaseChildSearchableService;

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
