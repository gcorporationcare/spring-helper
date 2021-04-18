package com.github.gcorporationcare.notest.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.github.gcorporationcare.notest.entity.Person;
import com.github.gcorporationcare.notest.repository.custom.CustomPersonRepository;

public interface PersonRepository extends PagingAndSortingRepository<Person, Long>, CustomPersonRepository, JpaSpecificationExecutor<Person> {

}
