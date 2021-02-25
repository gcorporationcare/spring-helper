package com.gcorp.notest.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.gcorp.notest.entity.Office;
import com.gcorp.notest.repository.custom.CustomOfficeRepository;

public interface OfficeRepository extends PagingAndSortingRepository<Office, Long>, CustomOfficeRepository, JpaSpecificationExecutor<Office> {

}
