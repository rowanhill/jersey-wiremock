package jerseywiremock.core.stub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;

public abstract class BaseRequestMocker {
    protected final WireMockServer wireMockServer;
    protected final ObjectMapper objectMapper;
    protected final MappingBuilder mappingBuilder;

    public BaseRequestMocker(WireMockServer wireMockServer, ObjectMapper objectMapper, MappingBuilder mappingBuilder) {
        this.wireMockServer = wireMockServer;
        this.objectMapper = objectMapper;
        this.mappingBuilder = mappingBuilder;
    }
}
