package io.jerseywiremock.core.stub.response;

import java.util.Collection;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;

import io.jerseywiremock.core.stub.request.Serializer;

public class GetListResponseStubber<Entity> extends CollectionResponseStubber<Entity, GetListResponseStubber<Entity>> {
    public GetListResponseStubber(
            WireMock wireMock,
            Serializer serializer,
            MappingBuilder mappingBuilder,
            ResponseDefinitionBuilder responseDefinitionBuilder,
            Collection<Entity> collection
    ) {
        super(wireMock, serializer, mappingBuilder, responseDefinitionBuilder, collection);
    }
}
