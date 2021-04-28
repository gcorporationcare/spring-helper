package com.github.gcorporationcare.web.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import com.github.gcorporationcare.ApiStarter;
import com.github.gcorporationcare.notest.common.RandomUtils;
import com.github.gcorporationcare.notest.config.H2Config;
import com.github.gcorporationcare.notest.dto.PersonDto;
import com.github.gcorporationcare.notest.entity.Person;
import com.github.gcorporationcare.notest.repository.PersonRepository;

@ActiveProfiles("test")
@SpringBootTest(classes = { ApiStarter.class, H2Config.class })
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class BaseDtoTest {

	@Autowired
	ModelMapper modelMapper;
	@Autowired
	PersonRepository personRepository;

	@Test
	void testMapping() {
		Person person = RandomUtils.randomPerson();
		PersonDto personDto = modelMapper.map(person, PersonDto.class);
		assertNull(person.getId());
		assertNull(personDto.getId());

		person = personRepository.save(person);
		personDto = modelMapper.map(person, PersonDto.class);
		assertEquals(personDto.getId(), person.getId());
		assertEquals(personDto.getCreated(), person.getCreated());
		assertEquals(personDto.getCreatedBy(), person.getCreatedBy());
		assertEquals(personDto.getUpdated(), person.getUpdated());
		assertEquals(personDto.getUpdatedBy(), person.getUpdatedBy());
	}
}
