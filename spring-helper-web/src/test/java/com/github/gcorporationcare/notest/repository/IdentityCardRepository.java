package com.github.gcorporationcare.notest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.github.gcorporationcare.notest.entity.IdentityCard;
import com.github.gcorporationcare.notest.repository.custom.CustomIdentityCardRepository;

public interface IdentityCardRepository extends PagingAndSortingRepository<IdentityCard, Long>, CustomIdentityCardRepository, JpaSpecificationExecutor<IdentityCard> {

	List<IdentityCard> findByPersonId(Long personId);
}
