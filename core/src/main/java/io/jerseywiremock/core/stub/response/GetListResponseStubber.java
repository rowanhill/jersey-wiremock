package io.jerseywiremock.core.stub.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;

import java.util.Collection;

public class GetListResponseStubber<Entity> extends CollectionResponseStubber<Entity, GetListResponseStubber<Entity>> {
    public GetListResponseStubber(
            WireMockServer wireMockServer,
            ObjectMapper objectMapper,
            MappingBuilder mappingBuilder,
            ResponseDefinitionBuilder responseDefinitionBuilder,
            Collection<Entity> collection
    ) {
        super(wireMockServer, objectMapper, mappingBuilder, responseDefinitionBuilder, collection);
    }
}
