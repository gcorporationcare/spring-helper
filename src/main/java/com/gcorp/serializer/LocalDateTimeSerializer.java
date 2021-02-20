package com.gcorp.serializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.gcorp.common.Utils;

public class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {
	@Override
	public void serialize(LocalDateTime value, JsonGenerator generator, SerializerProvider serializers)
			throws IOException {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Utils.API_DATETIME_FORMAT);
		ZonedDateTime dateTime = ZonedDateTime.of(value, ZoneId.of(Utils.UTC_ZONE));
		generator.writeString(dateTime.format(formatter));
	}
}
