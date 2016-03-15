package jerseywiremock.core.stub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;

import java.util.Collection;
import java.util.Collections;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

public class ListRequestMocker<Entity> {
    private final WireMockServer wireMockServer;
    private final ObjectMapper objectMapper;
    private final String urlPath;
    private final Collection<Entity> collection;

    public ListRequestMocker(
            WireMockServer wireMockServer,
            ObjectMapper objectMapper,
            String urlPath,
            Collection<Entity> collection
    ) {
        this.wireMockServer = wireMockServer;
        this.objectMapper = objectMapper;
        this.urlPath = urlPath;
        this.collection = collection;
    }

    public ListResponseMocker<Entity> andRespondWith(Entity... items) {
        MappingBuilder mappingBuilder = get(urlPathEqualTo(urlPath));
        Collections.addAll(collection, items);
        return new ListResponseMocker<Entity>(wireMockServer, objectMapper, mappingBuilder, collection);
    }
}
