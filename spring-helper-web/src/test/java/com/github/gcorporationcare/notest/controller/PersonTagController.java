package com.github.gcorporationcare.notest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.gcorporationcare.notest.dto.PersonTagDto;
import com.github.gcorporationcare.notest.entity.PersonTag;
import com.github.gcorporationcare.notest.repository.PersonTagRepository;
import com.github.gcorporationcare.notest.service.PersonTagService;
import com.github.gcorporationcare.web.controller.BaseChildRegistrableController;
import com.github.gcorporationcare.web.service.BaseChildSearchableService;

@RestController
@RequestMapping("/persons/{parent}/tags")
public class PersonTagController
		extends BaseChildRegistrableController<PersonTagDto, PersonTag, Long, Long, PersonTagRepository, Long> {

	@Autowired
	PersonTagService service;

	@Override
	public BaseChildSearchableService<PersonTag, Long, Long, PersonTagRepository> service() {
		return service;
	}
}
