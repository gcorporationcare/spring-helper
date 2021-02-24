package com.gcorp.notest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gcorp.domain.FieldFilter;

public abstract class BaseControllerTest {
	protected static final String CONTENT_TYPE = "Content-Type";
	protected static final String MULTIPLE_DELIMITER = "&ids=";
	@Autowired
	protected WebApplicationContext context;
	protected ObjectMapper mapper;
	protected MockMvc service;

	protected String read;
	protected String readByFilters;
	protected String readOne;
	protected String create;
	protected String createMultiple;
	protected String update;
	protected String patch;
	protected String delete;
	protected String deleteMultiple;

	public void setUp() {
		mapper = new ObjectMapper();
		SimpleFilterProvider filterProvider = new SimpleFilterProvider().addFilter(FieldFilter.JSON_FILTER_NAME,
				SimpleBeanPropertyFilter.serializeAll());
		mapper.setFilterProvider(filterProvider);
		mapper.registerModule(new JavaTimeModule());
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		mapper.configure(MapperFeature.USE_ANNOTATIONS, true);

		mapper.findAndRegisterModules();
		service = MockMvcBuilders.webAppContextSetup(context).build();
	}

	public String toJson(Object object, boolean disableAnnotation) throws JsonProcessingException {
		if (!disableAnnotation)
			return toJson(object);
		mapper.disable(MapperFeature.USE_ANNOTATIONS);
		String json = mapper.writeValueAsString(object);
		mapper.enable(MapperFeature.USE_ANNOTATIONS);
		return json;
	}

	public String toJson(Object object) throws JsonProcessingException {
		return mapper.writeValueAsString(object);
	}
}
