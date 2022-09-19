package io.jerseywiremock.core.stub.request;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;

import io.jerseywiremock.core.stub.response.GetSingleResponseStubber;

public class GetSingleRequestStubber<Entity>
        extends EmptyRequestSimpleResponseRequestStubber<Entity, GetSingleResponseStubber<Entity>>
{
    public GetSingleRequestStubber(
            WireMock wireMock,
            Serializer serializer,
            MappingBuilder mappingBuilder,
            ResponseDefinitionBuilder responseDefinitionBuilder
    ) {
        super(wireMock, serializer, mappingBuilder, responseDefinitionBuilder);
    }

    public GetSingleRequestStubber(
            WireMock wireMock,
            Serializer serializer,
            MappingBuilder mappingBuilder
    ) {
        super(wireMock, serializer, mappingBuilder);
    }

    @Override
    public GetSingleResponseStubber<Entity> andRespond() {
        return new GetSingleResponseStubber<>(wireMock, serializer, mappingBuilder, responseDefinitionBuilder);
    }
}
