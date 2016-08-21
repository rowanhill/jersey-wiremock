package io.jerseywiremock.core.stub.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import io.jerseywiremock.core.stub.response.PostResponseStubber;

public class PostRequestStubber<RequestEntity, ResponseEntity>
        extends RequestAndResponseRequestStubber<
        RequestEntity,
        ResponseEntity,
        PostResponseStubber<ResponseEntity>,
        PostRequestStubber<RequestEntity, ResponseEntity>
        >
{
    public PostRequestStubber(
            WireMockServer wireMockServer,
            ObjectMapper objectMapper,
            MappingBuilder mappingBuilder,
            ResponseDefinitionBuilder responseDefinitionBuilder
    ) {
        super(wireMockServer, objectMapper, mappingBuilder, responseDefinitionBuilder);
    }

    public PostRequestStubber(
            WireMockServer wireMockServer,
            ObjectMapper objectMapper,
            MappingBuilder mappingBuilder
    ) {
        super(wireMockServer, objectMapper, mappingBuilder);
    }

    @Override
    public PostResponseStubber<ResponseEntity> andRespond() {
        return new PostResponseStubber<>(wireMockServer, objectMapper, mappingBuilder, responseDefinitionBuilder);
    }
}
