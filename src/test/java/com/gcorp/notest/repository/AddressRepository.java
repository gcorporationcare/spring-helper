package com.gcorp.notest.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.gcorp.notest.entity.Address;
import com.gcorp.notest.repository.custom.CustomAddressRepository;

public interface AddressRepository extends PagingAndSortingRepository<Address, Long>, CustomAddressRepository, JpaSpecificationExecutor<Address> {

}
