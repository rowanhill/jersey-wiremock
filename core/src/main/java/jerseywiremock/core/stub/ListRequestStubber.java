package jerseywiremock.core.stub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;

import java.util.Collection;

public class ListRequestStubber<Entity> extends BaseRequestStubber {
    private final Collection<Entity> initialCollection;

    public ListRequestStubber(
            WireMockServer wireMockServer,
            ObjectMapper objectMapper,
            MappingBuilder mappingBuilder,
            Collection<Entity> initialCollection
    ) {
        super(wireMockServer, objectMapper, mappingBuilder);
        this.initialCollection = initialCollection;
    }

    public ListResponseStubber<Entity> andRespond() {
        return new ListResponseStubber<>(wireMockServer, objectMapper, mappingBuilder, initialCollection);
    }

    @SafeVarargs
    public final ListResponseStubber<Entity> andRespondWith(Entity... items) {
        return andRespond().withEntities(items);
    }
}
