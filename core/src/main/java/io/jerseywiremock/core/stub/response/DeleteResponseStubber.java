package io.jerseywiremock.core.stub.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;

public class DeleteResponseStubber<Entity> extends SimpleEntityResponseStubber<Entity, DeleteResponseStubber<Entity>> {
    public DeleteResponseStubber(
            WireMockServer wireMockServer,
            ObjectMapper objectMapper,
            MappingBuilder mappingBuilder,
            ResponseDefinitionBuilder responseDefinitionBuilder
    ) {
        super(wireMockServer, objectMapper, mappingBuilder, responseDefinitionBuilder);
    }
}
