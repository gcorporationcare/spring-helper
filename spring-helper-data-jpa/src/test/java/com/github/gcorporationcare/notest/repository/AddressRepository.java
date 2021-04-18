package com.github.gcorporationcare.notest.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.github.gcorporationcare.notest.entity.Address;
import com.github.gcorporationcare.notest.repository.custom.CustomAddressRepository;

public interface AddressRepository extends PagingAndSortingRepository<Address, Long>, CustomAddressRepository, JpaSpecificationExecutor<Address> {

}
