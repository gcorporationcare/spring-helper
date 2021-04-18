package com.github.gcorporationcare.data.serializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.github.gcorporationcare.data.common.Utils;

public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
	@Override
	public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Utils.API_DATETIME_FORMAT);
		ZonedDateTime dateTime = ZonedDateTime.parse(parser.getValueAsString(), formatter);
		return dateTime.toLocalDateTime();
	}
}
