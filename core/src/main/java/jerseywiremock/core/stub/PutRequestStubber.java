package jerseywiremock.core.stub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;

public class PutRequestStubber<RequestEntity, ResponseEntity>
        extends RequestAndResponseRequestStubber<
        RequestEntity,
        ResponseEntity,
        PutResponseStubber<ResponseEntity>,
        PutRequestStubber<RequestEntity, ResponseEntity>
        >
{
    public PutRequestStubber(
            WireMockServer wireMockServer,
            ObjectMapper objectMapper,
            MappingBuilder mappingBuilder,
            ResponseDefinitionBuilder responseDefinitionBuilder
    ) {
        super(wireMockServer, objectMapper, mappingBuilder, responseDefinitionBuilder);
    }

    public PutRequestStubber(
            WireMockServer wireMockServer,
            ObjectMapper objectMapper,
            MappingBuilder mappingBuilder
    ) {
        super(wireMockServer, objectMapper, mappingBuilder);
    }

    @Override
    public PutResponseStubber<ResponseEntity> andRespond() {
        return new PutResponseStubber<>(wireMockServer, objectMapper, mappingBuilder, responseDefinitionBuilder);
    }
}
