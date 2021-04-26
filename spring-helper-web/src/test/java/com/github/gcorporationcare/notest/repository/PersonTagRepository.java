package com.github.gcorporationcare.notest.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.github.gcorporationcare.notest.entity.PersonTag;
import com.github.gcorporationcare.notest.repository.custom.CustomPersonTagRepository;

public interface PersonTagRepository extends PagingAndSortingRepository<PersonTag, Long>, CustomPersonTagRepository,
		JpaSpecificationExecutor<PersonTag> {

}
