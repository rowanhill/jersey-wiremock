package jerseywiremock.core.stub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;

public class EmptyRequestSimpleResponseRequestStubber<Entity> extends BaseRequestStubber {
    public EmptyRequestSimpleResponseRequestStubber(
            WireMockServer wireMockServer,
            ObjectMapper objectMapper,
            MappingBuilder mappingBuilder
    ) {
        super(wireMockServer, objectMapper, mappingBuilder);
    }

    public SimpleEntityResponseStubber<Entity> andRespond() {
        return new SimpleEntityResponseStubber<>(wireMockServer, objectMapper, mappingBuilder);
    }

    public SimpleEntityResponseStubber<Entity> andRespondWith(Entity entity) {
        return andRespond().withEntity(entity);
    }
}
