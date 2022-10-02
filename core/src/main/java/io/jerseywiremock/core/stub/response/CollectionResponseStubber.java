package io.jerseywiremock.core.stub.response;

import java.util.Collection;
import java.util.Collections;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;

import io.jerseywiremock.core.stub.request.Serializer;

public abstract class CollectionResponseStubber<
        Entity,
        Self extends CollectionResponseStubber
        > extends BaseResponseStubber<Self>
{
    private final Collection<Entity> entities;

    public CollectionResponseStubber(
            WireMock wireMock,
            Serializer serializer,
            MappingBuilder mappingBuilder,
            ResponseDefinitionBuilder responseDefinitionBuilder,
            Collection<Entity> entities
    ) {
        super(wireMock, serializer, mappingBuilder, responseDefinitionBuilder);
        this.entities = entities;
    }

    @SafeVarargs
    public final Self withEntities(Entity... items) {
        Collections.addAll(entities, items);
        //noinspection unchecked
        return (Self) this;
    }

    @Override
    protected void amendResponseDefinition(ResponseDefinitionBuilder responseDefinitionBuilder) {
        String bodyString = serializer.serialize(entities);
        responseDefinitionBuilder.withBody(bodyString);
    }
}
