package jerseywiremock.core.stub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import jerseywiremock.core.RequestMappingDescriptor;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

public class GetRequestMocker<Entity> {
    private final WireMockServer wireMockServer;
    private final ObjectMapper objectMapper;
    private final MappingBuilder mappingBuilder;

    public GetRequestMocker(WireMockServer wireMockServer, ObjectMapper objectMapper, RequestMappingDescriptor mappingDescriptor) {
        this.wireMockServer = wireMockServer;
        this.objectMapper = objectMapper;

        this.mappingBuilder = createMappingBuilder(mappingDescriptor);
    }

    public GetResponseMocker<Entity> andRespondWith(Entity entity) {
        return new GetResponseMocker<Entity>(wireMockServer, objectMapper, mappingBuilder, entity);
    }

    private MappingBuilder createMappingBuilder(RequestMappingDescriptor mappingDescriptor) {
        MappingBuilder mappingBuilder = get(urlPathEqualTo(mappingDescriptor.getUrlPath()));
        for (Map.Entry<String, String> entry : mappingDescriptor.getQueryParams().entrySet()) {
            mappingBuilder.withQueryParam(entry.getKey(), equalTo(entry.getValue()));
        }
        return mappingBuilder;
    }
}
