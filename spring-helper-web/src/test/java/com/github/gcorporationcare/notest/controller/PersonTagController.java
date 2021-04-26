package com.github.gcorporationcare.notest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.gcorporationcare.notest.dto.PersonTagDto;
import com.github.gcorporationcare.notest.entity.PersonTag;
import com.github.gcorporationcare.notest.repository.PersonTagRepository;
import com.github.gcorporationcare.notest.service.PersonTagService;
import com.github.gcorporationcare.web.controller.BaseSlaveRegistrableController;
import com.github.gcorporationcare.web.service.BaseSlaveSearchableService;

@RestController
@RequestMapping("/persons/{master}/tags")
public class PersonTagController
		extends BaseSlaveRegistrableController<PersonTagDto, PersonTag, Long, PersonTagRepository, Long> {

	@Autowired
	PersonTagService service;

	@Override
	public BaseSlaveSearchableService<PersonTag, Long, PersonTagRepository, Long> service() {
		return service;
	}
}
