package jerseywiremock.core.stub.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import jerseywiremock.core.stub.response.SimpleEntityResponseStubber;

public abstract class EmptyRequestSimpleResponseRequestStubber<
        Entity,
        ResponseStubber extends SimpleEntityResponseStubber<Entity, ResponseStubber>
        >
        extends BaseRequestStubber<ResponseStubber>
{
    public EmptyRequestSimpleResponseRequestStubber(
            WireMockServer wireMockServer,
            ObjectMapper objectMapper,
            MappingBuilder mappingBuilder,
            ResponseDefinitionBuilder responseDefinitionBuilder
    ) {
        super(wireMockServer, objectMapper, mappingBuilder, responseDefinitionBuilder);
    }

    public EmptyRequestSimpleResponseRequestStubber(
            WireMockServer wireMockServer,
            ObjectMapper objectMapper,
            MappingBuilder mappingBuilder
    ) {
        super(wireMockServer, objectMapper, mappingBuilder);
    }

    public ResponseStubber andRespondWith(Entity entity) {
        return andRespond().withEntity(entity);
    }
}
