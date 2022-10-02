package io.jerseywiremock.core.stub.request;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;

import io.jerseywiremock.core.stub.response.SimpleEntityResponseStubber;

public abstract class EmptyRequestSimpleResponseRequestStubber<
        Entity,
        ResponseStubber extends SimpleEntityResponseStubber<Entity, ResponseStubber>
        >
        extends BaseRequestStubber<ResponseStubber>
{
    public EmptyRequestSimpleResponseRequestStubber(
            WireMock wireMock,
            Serializer serializer,
            MappingBuilder mappingBuilder,
            ResponseDefinitionBuilder responseDefinitionBuilder
    ) {
        super(wireMock, serializer, mappingBuilder, responseDefinitionBuilder);
    }

    public EmptyRequestSimpleResponseRequestStubber(
            WireMock wireMock,
            Serializer serializer,
            MappingBuilder mappingBuilder
    ) {
        super(wireMock, serializer, mappingBuilder);
    }

    public ResponseStubber andRespondWith(Entity entity) {
        return andRespond().withEntity(entity);
    }
}
