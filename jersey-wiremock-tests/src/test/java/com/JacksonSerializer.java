package com;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jerseywiremock.core.stub.request.Serializer;

public class JacksonSerializer implements Serializer {
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	@Override
	public String serialize(Object object) {
		try {
			return objectMapper.writeValueAsString(object);
		}
		catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
}
