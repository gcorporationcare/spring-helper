package com.github.gcorporationcare.data.serializer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;

class LocalDateTimeDeserializerTest {
	@Test
	void testDeserialize() throws IOException {
		LocalDateTime date = LocalDateTime.of(LocalDate.of(2021, 1, 1), LocalTime.of(0, 0));
		LocalDateTimeDeserializer serializer = new LocalDateTimeDeserializer();
		final String dateString = "\"2021-01-01T00:00:00.0000+0000\"";
		InputStream stream = new ByteArrayInputStream(dateString.getBytes(StandardCharsets.UTF_8));
		JsonParser parser = new JsonFactory().createParser(stream);
		DeserializationContext context = new ObjectMapper().getDeserializationContext();
		// Needed for reading value
		parser.nextToken();
		assertEquals(date, serializer.deserialize(parser, context));
	}
}
