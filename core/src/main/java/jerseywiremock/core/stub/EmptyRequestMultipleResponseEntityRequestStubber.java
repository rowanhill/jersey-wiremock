package jerseywiremock.core.stub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;

import java.util.Collection;

public class EmptyRequestMultipleResponseEntityRequestStubber<Entity> extends BaseRequestStubber {
    private final Collection<Entity> initialCollection;

    public EmptyRequestMultipleResponseEntityRequestStubber(
            WireMockServer wireMockServer,
            ObjectMapper objectMapper,
            MappingBuilder mappingBuilder,
            Collection<Entity> initialCollection
    ) {
        super(wireMockServer, objectMapper, mappingBuilder);
        this.initialCollection = initialCollection;
    }

    public MultipleEntityResponseStubber<Entity> andRespond() {
        return new MultipleEntityResponseStubber<>(wireMockServer, objectMapper, mappingBuilder, initialCollection);
    }

    @SafeVarargs
    public final MultipleEntityResponseStubber<Entity> andRespondWith(Entity... items) {
        return andRespond().withEntities(items);
    }
}
