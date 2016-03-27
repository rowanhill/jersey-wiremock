package jerseywiremock.core.stub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;

public abstract class EmptyRequestAndResponseRequestStubber<
        ResponseStubber extends EmptyResponseStubber<ResponseStubber>
        > extends BaseRequestStubber<ResponseStubber>
{
    public EmptyRequestAndResponseRequestStubber(
            WireMockServer wireMockServer,
            ObjectMapper objectMapper,
            MappingBuilder mappingBuilder
    ) {
        super(wireMockServer, objectMapper, mappingBuilder);
    }
}
