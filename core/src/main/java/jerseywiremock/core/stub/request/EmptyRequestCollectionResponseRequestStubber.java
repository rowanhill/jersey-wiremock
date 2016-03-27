package jerseywiremock.core.stub.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import jerseywiremock.core.stub.response.CollectionResponseStubber;

import java.util.Collection;

public abstract class EmptyRequestCollectionResponseRequestStubber<
        Entity,
        ResponseStubber extends CollectionResponseStubber<Entity, ResponseStubber>
        > extends BaseRequestStubber<ResponseStubber>
{
    protected final Collection<Entity> initialCollection;

    public EmptyRequestCollectionResponseRequestStubber(
            WireMockServer wireMockServer,
            ObjectMapper objectMapper,
            MappingBuilder mappingBuilder,
            ResponseDefinitionBuilder responseDefinitionBuilder,
            Collection<Entity> initialCollection
    ) {
        super(wireMockServer, objectMapper, mappingBuilder, responseDefinitionBuilder);
        this.initialCollection = initialCollection;
    }

    public EmptyRequestCollectionResponseRequestStubber(
            WireMockServer wireMockServer,
            ObjectMapper objectMapper,
            MappingBuilder mappingBuilder,
            Collection<Entity> initialCollection
    ) {
        super(wireMockServer, objectMapper, mappingBuilder);
        this.initialCollection = initialCollection;
    }

    @SafeVarargs
    public final ResponseStubber andRespondWith(Entity... items) {
        return andRespond().withEntities(items);
    }
}
