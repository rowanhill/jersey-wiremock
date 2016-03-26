package jerseywiremock.core.stub;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;

import java.util.Collection;
import java.util.Collections;

public class ListResponseStubber<Entity> extends BaseResponseStubber<ListResponseStubber<Entity>> {
    private final Collection<Entity> entities;

    public ListResponseStubber(
            WireMockServer wireMockServer,
            ObjectMapper objectMapper,
            MappingBuilder mappingBuilder,
            Collection<Entity> initialCollection
    ) {
        super(wireMockServer, objectMapper, mappingBuilder);
        this.entities = initialCollection;
    }

    @SafeVarargs
    public final ListResponseStubber<Entity> withEntities(Entity... items) {
        Collections.addAll(entities, items);
        return this;
    }

    @Override
    protected void amendResponseDefinition(ResponseDefinitionBuilder responseDefinitionBuilder)
            throws JsonProcessingException
    {
        String bodyString = objectMapper.writeValueAsString(entities);
        responseDefinitionBuilder.withBody(bodyString);
    }
}
