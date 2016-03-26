package jerseywiremock.core.stub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;

import java.util.Collection;

public class EmptyRequestCollectionResponseRequestStubber<Entity> extends BaseRequestStubber {
    private final Collection<Entity> initialCollection;

    public EmptyRequestCollectionResponseRequestStubber(
            WireMockServer wireMockServer,
            ObjectMapper objectMapper,
            MappingBuilder mappingBuilder,
            Collection<Entity> initialCollection
    ) {
        super(wireMockServer, objectMapper, mappingBuilder);
        this.initialCollection = initialCollection;
    }

    public CollectionResponseStubber<Entity> andRespond() {
        return new CollectionResponseStubber<>(wireMockServer, objectMapper, mappingBuilder, initialCollection);
    }

    @SafeVarargs
    public final CollectionResponseStubber<Entity> andRespondWith(Entity... items) {
        return andRespond().withEntities(items);
    }
}
