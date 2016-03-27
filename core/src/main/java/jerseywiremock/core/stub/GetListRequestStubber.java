package jerseywiremock.core.stub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;

import java.util.Collection;

public class GetListRequestStubber<Entity>
        extends EmptyRequestCollectionResponseRequestStubber<Entity, GetListResponseStubber<Entity>>
{
    public GetListRequestStubber(
            WireMockServer wireMockServer,
            ObjectMapper objectMapper,
            MappingBuilder mappingBuilder,
            ResponseDefinitionBuilder responseDefinitionBuilder,
            Collection<Entity> initialCollection
    ) {
        super(wireMockServer, objectMapper, mappingBuilder, responseDefinitionBuilder, initialCollection);
    }

    public GetListRequestStubber(
            WireMockServer wireMockServer,
            ObjectMapper objectMapper,
            MappingBuilder mappingBuilder,
            Collection<Entity> initialCollection
    ) {
        super(wireMockServer, objectMapper, mappingBuilder, initialCollection);
    }

    @Override
    public GetListResponseStubber<Entity> andRespond() {
        return new GetListResponseStubber<>(
                wireMockServer,
                objectMapper,
                mappingBuilder,
                responseDefinitionBuilder,
                initialCollection);
    }
}
