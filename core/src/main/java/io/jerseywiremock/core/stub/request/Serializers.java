package io.jerseywiremock.core.stub.request;

import java.util.concurrent.ConcurrentHashMap;

public class Serializers {
	private final ConcurrentHashMap<String, Serializer> serializerByContentType = new ConcurrentHashMap<>();

	public void addSerializer(String contentType, Serializer serializer) {
		if (contentType == null || contentType.isEmpty()) {
			throw new IllegalArgumentException("Content-Type, associated with serializer is null");
		}
		if (serializer == null) {
			throw new IllegalArgumentException("Serializer is null!");
		}
		serializerByContentType.put(contentType, serializer);
	}

	public Serializer getSerializerByContentType(String contentType) {
		return serializerByContentType.get(contentType);
	}

}
