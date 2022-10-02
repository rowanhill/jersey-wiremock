package io.jerseywiremock.core.stub.response;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;

import io.jerseywiremock.core.stub.request.Serializer;
import io.jerseywiremock.core.stub.request.Serializers;

public class GetSingleResponseStubber<Entity> extends SimpleEntityResponseStubber<Entity, GetSingleResponseStubber<Entity>> {
    public GetSingleResponseStubber(
            WireMock wireMock,
            Serializer serializer,
            MappingBuilder mappingBuilder,
            ResponseDefinitionBuilder responseDefinitionBuilder
    ) {
        super(wireMock, serializer, mappingBuilder, responseDefinitionBuilder);
    }
}
