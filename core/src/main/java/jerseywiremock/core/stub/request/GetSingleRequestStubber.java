package jerseywiremock.core.stub.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import jerseywiremock.core.stub.response.GetSingleResponseStubber;

public class GetSingleRequestStubber<Entity>
        extends EmptyRequestSimpleResponseRequestStubber<Entity, GetSingleResponseStubber<Entity>>
{
    public GetSingleRequestStubber(
            WireMockServer wireMockServer,
            ObjectMapper objectMapper,
            MappingBuilder mappingBuilder,
            ResponseDefinitionBuilder responseDefinitionBuilder
    ) {
        super(wireMockServer, objectMapper, mappingBuilder, responseDefinitionBuilder);
    }

    public GetSingleRequestStubber(
            WireMockServer wireMockServer,
            ObjectMapper objectMapper,
            MappingBuilder mappingBuilder
    ) {
        super(wireMockServer, objectMapper, mappingBuilder);
    }

    @Override
    public GetSingleResponseStubber<Entity> andRespond() {
        return new GetSingleResponseStubber<>(wireMockServer, objectMapper, mappingBuilder, responseDefinitionBuilder);
    }
}
