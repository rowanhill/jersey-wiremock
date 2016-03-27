package jerseywiremock.core.stub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;

public class DeleteRequestStubber extends EmptyRequestAndResponseRequestStubber<DeleteResponseStubber> {
    public DeleteRequestStubber(
            WireMockServer wireMockServer,
            ObjectMapper objectMapper,
            MappingBuilder mappingBuilder
    ) {
        super(wireMockServer, objectMapper, mappingBuilder);
    }

    @Override
    public DeleteResponseStubber andRespond() {
        return new DeleteResponseStubber(wireMockServer, objectMapper, mappingBuilder);
    }
}
