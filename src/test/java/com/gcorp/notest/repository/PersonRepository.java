package com.gcorp.notest.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.gcorp.notest.entity.Person;
import com.gcorp.notest.repository.custom.CustomPersonRepository;

public interface PersonRepository extends PagingAndSortingRepository<Person, Long>, CustomPersonRepository, JpaSpecificationExecutor<Person> {

}
