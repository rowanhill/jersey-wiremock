package io.jerseywiremock.core.stub.request;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;

import io.jerseywiremock.core.stub.response.DeleteResponseStubber;

public class DeleteRequestStubber<Entity>
        extends EmptyRequestSimpleResponseRequestStubber<Entity, DeleteResponseStubber<Entity>>
{
    public DeleteRequestStubber(
            WireMock wireMock,
            Serializer serializer,
            MappingBuilder mappingBuilder,
            ResponseDefinitionBuilder responseDefinitionBuilder
    ) {
        super(wireMock, serializer, mappingBuilder, responseDefinitionBuilder);
    }

    public DeleteRequestStubber(
            WireMock wireMock,
            Serializer serializer,
            MappingBuilder mappingBuilder
    ) {
        super(wireMock, serializer, mappingBuilder);
    }

    @Override
    public DeleteResponseStubber<Entity> andRespond() {
        return new DeleteResponseStubber<>(wireMock, serializer, mappingBuilder, responseDefinitionBuilder);
    }
}
