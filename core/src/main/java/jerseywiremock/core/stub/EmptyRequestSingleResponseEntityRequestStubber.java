package jerseywiremock.core.stub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;

public class EmptyRequestSingleResponseEntityRequestStubber<Entity> extends BaseRequestStubber {
    public EmptyRequestSingleResponseEntityRequestStubber(
            WireMockServer wireMockServer,
            ObjectMapper objectMapper,
            MappingBuilder mappingBuilder
    ) {
        super(wireMockServer, objectMapper, mappingBuilder);
    }

    public SingleEntityResponseStubber<Entity> andRespond() {
        return new SingleEntityResponseStubber<>(wireMockServer, objectMapper, mappingBuilder);
    }

    public SingleEntityResponseStubber<Entity> andRespondWith(Entity entity) {
        return andRespond().withEntity(entity);
    }
}
