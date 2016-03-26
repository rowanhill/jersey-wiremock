package jerseywiremock.core.stub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;

import java.util.Collection;

public class GetListResponseStubber<Entity> extends CollectionResponseStubber<Entity, GetListResponseStubber<Entity>> {
    public GetListResponseStubber(
            WireMockServer wireMockServer,
            ObjectMapper objectMapper,
            MappingBuilder mappingBuilder,
            Collection<Entity> initialCollection
    ) {
        super(wireMockServer, objectMapper, mappingBuilder, initialCollection);
    }
}
