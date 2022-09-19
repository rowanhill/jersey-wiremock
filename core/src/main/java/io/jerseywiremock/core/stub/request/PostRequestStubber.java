package io.jerseywiremock.core.stub.request;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;

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
            WireMock wireMock,
            Serializer serializer,
            MappingBuilder mappingBuilder,
            ResponseDefinitionBuilder responseDefinitionBuilder
    ) {
        super(wireMock, serializer, mappingBuilder, responseDefinitionBuilder);
    }

    public PostRequestStubber(
            WireMock wireMock,
            Serializer serializer,
            MappingBuilder mappingBuilder
    ) {
        super(wireMock, serializer, mappingBuilder);
    }

    @Override
    public PostResponseStubber<ResponseEntity> andRespond() {
        return new PostResponseStubber<>(wireMock, serializer, mappingBuilder, responseDefinitionBuilder);
    }
}
