package jerseywiremock.core.stub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import jerseywiremock.core.RequestMappingDescriptor;
import jerseywiremock.core.stub.verbs.GetMappingBuilderStrategy;

import java.util.Collection;

public class ListRequestMocker<Entity> extends BaseRequestMocker {
    private final Collection<Entity> initialCollection;

    public ListRequestMocker(
            WireMockServer wireMockServer,
            ObjectMapper objectMapper,
            RequestMappingDescriptor mappingDescriptor,
            Collection<Entity> initialCollection
    ) {
        super(wireMockServer, objectMapper, mappingDescriptor, new GetMappingBuilderStrategy());
        this.initialCollection = initialCollection;
    }

    public ListRequestMocker(
            WireMockServer wireMockServer,
            ObjectMapper objectMapper,
            MappingBuilder mappingBuilder,
            Collection<Entity> initialCollection
    ) {
        super(wireMockServer, objectMapper, mappingBuilder);
        this.initialCollection = initialCollection;
    }

    public ListResponseMocker<Entity> andRespond() {
        return new ListResponseMocker<>(wireMockServer, objectMapper, mappingBuilder, initialCollection);
    }

    @SafeVarargs
    public final ListResponseMocker<Entity> andRespondWith(Entity... items) {
        return andRespond().withEntities(items);
    }
}
