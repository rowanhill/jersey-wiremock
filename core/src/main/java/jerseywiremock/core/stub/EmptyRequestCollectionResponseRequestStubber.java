package jerseywiremock.core.stub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;

import java.util.Collection;

public abstract class EmptyRequestCollectionResponseRequestStubber<
        Entity,
        ResponseStubber extends CollectionResponseStubber<Entity, ResponseStubber>
        > extends BaseRequestStubber
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

    public abstract ResponseStubber andRespond();

    @SafeVarargs
    public final ResponseStubber andRespondWith(Entity... items) {
        return andRespond().withEntities(items);
    }
}
