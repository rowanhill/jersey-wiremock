package jerseywiremock.core.stub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import jerseywiremock.core.RequestMappingDescriptor;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

public class ListRequestMocker<Entity> {
    private final WireMockServer wireMockServer;
    private final ObjectMapper objectMapper;
    private final MappingBuilder mappingBuilder;
    private final Collection<Entity> collection;

    public ListRequestMocker(
            WireMockServer wireMockServer,
            ObjectMapper objectMapper,
            RequestMappingDescriptor mappingDescriptor,
            Collection<Entity> collection
    ) {
        this.wireMockServer = wireMockServer;
        this.objectMapper = objectMapper;
        this.mappingBuilder = createMappingBuilder(mappingDescriptor);
        this.collection = collection;
    }

    public ListResponseMocker<Entity> andRespondWith(Entity... items) {
        Collections.addAll(collection, items);
        return new ListResponseMocker<Entity>(wireMockServer, objectMapper, mappingBuilder, collection);
    }

    private MappingBuilder createMappingBuilder(RequestMappingDescriptor mappingDescriptor) {
        MappingBuilder mappingBuilder = get(urlPathEqualTo(mappingDescriptor.getUrlPath()));
        for (Map.Entry<String, String> entry : mappingDescriptor.getQueryParams().entrySet()) {
            mappingBuilder.withQueryParam(entry.getKey(), equalTo(entry.getValue()));
        }
        return mappingBuilder;
    }
}
