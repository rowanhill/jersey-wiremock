package jerseywiremock.core.stub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import jerseywiremock.core.RequestMappingDescriptor;
import jerseywiremock.core.stub.verbs.GetMappingBuilderStrategy;

import java.util.Collection;
import java.util.Collections;

public class ListRequestMocker<Entity> extends BaseRequestMocker {
    private final Collection<Entity> collection;

    public ListRequestMocker(
            WireMockServer wireMockServer,
            ObjectMapper objectMapper,
            RequestMappingDescriptor mappingDescriptor,
            Collection<Entity> collection
    ) {
        super(wireMockServer, objectMapper, mappingDescriptor, new GetMappingBuilderStrategy());
        this.collection = collection;
    }

    public ListResponseMocker<Entity> andRespondWith(Entity... items) {
        Collections.addAll(collection, items);
        return new ListResponseMocker<Entity>(wireMockServer, objectMapper, mappingBuilder, collection);
    }
}
