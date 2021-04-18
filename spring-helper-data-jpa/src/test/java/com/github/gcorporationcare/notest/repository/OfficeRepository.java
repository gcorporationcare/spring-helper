package com.github.gcorporationcare.notest.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.github.gcorporationcare.notest.entity.Office;
import com.github.gcorporationcare.notest.repository.custom.CustomOfficeRepository;

public interface OfficeRepository extends PagingAndSortingRepository<Office, Long>, CustomOfficeRepository, JpaSpecificationExecutor<Office> {

}
