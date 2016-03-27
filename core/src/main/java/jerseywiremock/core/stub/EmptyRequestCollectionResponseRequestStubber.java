package jerseywiremock.core.stub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;

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
