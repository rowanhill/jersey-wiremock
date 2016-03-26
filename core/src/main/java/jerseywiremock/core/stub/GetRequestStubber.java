package jerseywiremock.core.stub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;

public class GetRequestStubber<Entity> extends BaseRequestStubber {
    public GetRequestStubber(WireMockServer wireMockServer, ObjectMapper objectMapper, MappingBuilder mappingBuilder) {
        super(wireMockServer, objectMapper, mappingBuilder);
    }

    public GetResponseStubber<Entity> andRespond() {
        return new GetResponseStubber<>(wireMockServer, objectMapper, mappingBuilder);
    }

    public GetResponseStubber<Entity> andRespondWith(Entity entity) {
        return andRespond().withEntity(entity);
    }
}
