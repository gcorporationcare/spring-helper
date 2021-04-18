package com.github.gcorporationcare.notest.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.github.gcorporationcare.notest.entity.Promotion;
import com.github.gcorporationcare.notest.repository.custom.CustomPromotionRepository;

public interface PromotionRepository extends PagingAndSortingRepository<Promotion, Long>, CustomPromotionRepository, JpaSpecificationExecutor<Promotion> {

}
