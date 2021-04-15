package com.gcorp.serializer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;

class LocalDateTimeSerializerTest {

	@Test
	void testSerialize() throws IOException {
		LocalDateTime date = LocalDateTime.of(LocalDate.of(2021, 1, 1), LocalTime.of(0, 0));
		LocalDateTimeSerializer serializer = new LocalDateTimeSerializer();
		Writer jsonWriter = new StringWriter();
		JsonGenerator generator = new JsonFactory().createGenerator(jsonWriter);
		SerializerProvider serializers = new ObjectMapper().getSerializerProvider();
		serializer.serialize(date, generator, serializers);
		generator.flush();
		assertEquals("\"2021-01-01T00:00:00.0000+0000\"", jsonWriter.toString());
	}
}
