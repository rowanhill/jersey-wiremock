package jerseywiremock.core.stub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;

public abstract class EmptyRequestSimpleResponseRequestStubber<
        Entity,
        ResponseStubber extends SimpleEntityResponseStubber<Entity, ResponseStubber>
        >
        extends BaseRequestStubber
{
    public EmptyRequestSimpleResponseRequestStubber(
            WireMockServer wireMockServer,
            ObjectMapper objectMapper,
            MappingBuilder mappingBuilder
    ) {
        super(wireMockServer, objectMapper, mappingBuilder);
    }

    public abstract ResponseStubber andRespond();

    public ResponseStubber andRespondWith(Entity entity) {
        return andRespond().withEntity(entity);
    }
}
