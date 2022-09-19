package io.jerseywiremock.core.stub.request;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;

import io.jerseywiremock.core.stub.response.PutResponseStubber;

public class PutRequestStubber<RequestEntity, ResponseEntity>
        extends RequestAndResponseRequestStubber<
        RequestEntity,
        ResponseEntity,
        PutResponseStubber<ResponseEntity>,
        PutRequestStubber<RequestEntity, ResponseEntity>
        >
{
    public PutRequestStubber(
            WireMock wireMock,
            Serializer serializer,
            MappingBuilder mappingBuilder,
            ResponseDefinitionBuilder responseDefinitionBuilder
    ) {
        super(wireMock, serializer, mappingBuilder, responseDefinitionBuilder);
    }

    public PutRequestStubber(
            WireMock wireMock,
            Serializer serializer,
            MappingBuilder mappingBuilder
    ) {
        super(wireMock, serializer, mappingBuilder);
    }

    @Override
    public PutResponseStubber<ResponseEntity> andRespond() {
        return new PutResponseStubber<>(wireMock, serializer, mappingBuilder, responseDefinitionBuilder);
    }
}
