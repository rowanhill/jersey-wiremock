package io.jerseywiremock.core.stub.request;

import java.util.Collection;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;

import io.jerseywiremock.core.stub.response.CollectionResponseStubber;

public abstract class EmptyRequestCollectionResponseRequestStubber<
        Entity,
        ResponseStubber extends CollectionResponseStubber<Entity, ResponseStubber>
        > extends BaseRequestStubber<ResponseStubber>
{
    protected final Collection<Entity> initialCollection;

    public EmptyRequestCollectionResponseRequestStubber(
            WireMock wireMock,
            Serializer serializer,
            MappingBuilder mappingBuilder,
            ResponseDefinitionBuilder responseDefinitionBuilder,
            Collection<Entity> initialCollection
    ) {
        super(wireMock, serializer, mappingBuilder, responseDefinitionBuilder);
        this.initialCollection = initialCollection;
    }

    public EmptyRequestCollectionResponseRequestStubber(
            WireMock wireMock,
            Serializer serializer,
            MappingBuilder mappingBuilder,
            Collection<Entity> initialCollection
    ) {
        super(wireMock, serializer, mappingBuilder);
        this.initialCollection = initialCollection;
    }

    @SafeVarargs
    public final ResponseStubber andRespondWith(Entity... items) {
        return andRespond().withEntities(items);
    }
}
