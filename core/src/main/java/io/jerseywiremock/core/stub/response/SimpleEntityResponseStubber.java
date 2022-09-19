package io.jerseywiremock.core.stub.response;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;

import io.jerseywiremock.core.stub.request.Serializer;

public abstract class SimpleEntityResponseStubber<
        Entity,
        Self extends SimpleEntityResponseStubber<Entity, Self>
        > extends BaseResponseStubber<Self>
{
    private Entity entity;

    public SimpleEntityResponseStubber(
            WireMock wireMock,
            Serializer serializer,
            MappingBuilder mappingBuilder,
            ResponseDefinitionBuilder responseDefinitionBuilder
    ) {
        super(wireMock, serializer, mappingBuilder, responseDefinitionBuilder);
    }

    public Self withEntity(Entity entity) {
        this.entity = entity;
        //noinspection unchecked
        return (Self) this;
    }

    @Override
    protected void amendResponseDefinition(ResponseDefinitionBuilder responseDefinitionBuilder) {
        String bodyString = serializer.serialize(entity);
        responseDefinitionBuilder.withBody(bodyString);
    }
}
