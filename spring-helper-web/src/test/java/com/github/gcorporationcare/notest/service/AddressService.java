package com.github.gcorporationcare.notest.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.github.gcorporationcare.notest.entity.Address;
import com.github.gcorporationcare.notest.entity.Person;
import com.github.gcorporationcare.notest.repository.AddressRepository;
import com.github.gcorporationcare.notest.repository.PersonRepository;
import com.github.gcorporationcare.web.exception.RequestException;
import com.github.gcorporationcare.web.service.BaseChildRegistrableService;

import lombok.NonNull;

@Service
public class AddressService extends BaseChildRegistrableService<Address, Long, Long, AddressRepository, Person> {
	@Autowired
	PersonRepository personRepository;
	@Autowired
	AddressRepository addressRepository;

	@Override
	public AddressRepository repository() {
		return addressRepository;
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

	@Override
	public Optional<Person> findParent(@NonNull Long parentId) {
		return personRepository.findById(parentId);
	}
}
