package io.jerseywiremock.core.stub.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;

import java.util.Collection;
import java.util.Collections;

public abstract class CollectionResponseStubber<
        Entity,
        Self extends CollectionResponseStubber
        > extends BaseResponseStubber<Self>
{
    private final Collection<Entity> entities;

    public CollectionResponseStubber(
            WireMockServer wireMockServer,
            ObjectMapper objectMapper,
            MappingBuilder mappingBuilder,
            ResponseDefinitionBuilder responseDefinitionBuilder,
            Collection<Entity> entities
    ) {
        super(wireMockServer, objectMapper, mappingBuilder, responseDefinitionBuilder);
        this.entities = entities;
    }

    @SafeVarargs
    public final Self withEntities(Entity... items) {
        Collections.addAll(entities, items);
        //noinspection unchecked
        return (Self) this;
    }

    @Override
    protected void amendResponseDefinition(ResponseDefinitionBuilder responseDefinitionBuilder)
            throws JsonProcessingException
    {
        String bodyString = objectMapper.writeValueAsString(entities);
        responseDefinitionBuilder.withBody(bodyString);
    }
}
