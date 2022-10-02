package io.jerseywiremock.core.stub.request;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;

import io.jerseywiremock.core.stub.response.GetListResponseStubber;

import java.util.Collection;

public class GetListRequestStubber<Entity>
        extends EmptyRequestCollectionResponseRequestStubber<Entity, GetListResponseStubber<Entity>>
{
    public GetListRequestStubber(
            WireMock wireMock,
            Serializer serializer,
            MappingBuilder mappingBuilder,
            ResponseDefinitionBuilder responseDefinitionBuilder,
            Collection<Entity> initialCollection
    ) {
        super(wireMock, serializer, mappingBuilder, responseDefinitionBuilder, initialCollection);
    }

    public GetListRequestStubber(
            WireMock wireMock,
            Serializer serializer,
            MappingBuilder mappingBuilder,
            Collection<Entity> initialCollection
    ) {
        super(wireMock, serializer, mappingBuilder, initialCollection);
    }

    @Override
    public GetListResponseStubber<Entity> andRespond() {
        return new GetListResponseStubber<>(
                wireMock,
                serializer,
                mappingBuilder,
                responseDefinitionBuilder,
                initialCollection);
    }
}
