package com.gcorp.notest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.gcorp.exception.RequestException;
import com.gcorp.notest.entity.Address;
import com.gcorp.notest.entity.Person;
import com.gcorp.notest.repository.AddressRepository;
import com.gcorp.notest.repository.PersonRepository;
import com.gcorp.service.BaseChildRegistrableService;

import lombok.NonNull;

@Service
public class AddressService
		extends BaseChildRegistrableService<Address, Long, Person, Long, AddressRepository, PersonRepository> {
	@Autowired
	PersonRepository personRepository;
	@Autowired
	AddressRepository addressRepository;

	@Override
	public AddressRepository repository() {
		return addressRepository;
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
	public void canCreate(@NonNull Long parentId, @NonNull Address child) {
		child.setActive(true);
	}

	@Override
	public void canUpdate(@NonNull Long parentId, @NonNull Address child, @NonNull Address savedChild) {
		// Test class : nothing here
	}

	@Override
	public void canDelete(@NonNull Long parentId, @NonNull Address child) {
		if (!child.isActive()) {
			return;
		}
		throw new RequestException("Cannot delete active address", HttpStatus.FORBIDDEN);
	}

	@Override
	public String getParentIdField() {
		return "person.id";
	}
}
