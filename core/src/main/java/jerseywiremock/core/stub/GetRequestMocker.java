package jerseywiremock.core.stub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

public class GetRequestMocker<Entity> {
    private final WireMockServer wireMockServer;
    private final ObjectMapper objectMapper;
    private final String urlPath;

    public GetRequestMocker(WireMockServer wireMockServer, ObjectMapper objectMapper, String urlPath) {
        this.wireMockServer = wireMockServer;
        this.objectMapper = objectMapper;
        this.urlPath = urlPath;
    }

    public GetResponseMocker<Entity> andRespondWith(Entity entity) {
        MappingBuilder mappingBuilder = get(urlPathEqualTo(urlPath));
        return new GetResponseMocker<Entity>(wireMockServer, objectMapper, mappingBuilder, entity);
    }
}
