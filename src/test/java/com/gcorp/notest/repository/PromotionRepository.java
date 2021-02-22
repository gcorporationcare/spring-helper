package com.gcorp.notest.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.gcorp.notest.entity.Promotion;
import com.gcorp.notest.repository.custom.CustomPromotionRepository;

public interface PromotionRepository extends PagingAndSortingRepository<Promotion, Long>, CustomPromotionRepository, JpaSpecificationExecutor<Promotion> {

}
