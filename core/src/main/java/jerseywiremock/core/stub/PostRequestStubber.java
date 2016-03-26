package jerseywiremock.core.stub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;

public class PostRequestStubber<RequestEntity, ResponseEntity>
        extends RequestAndResponseRequestStubber<
        RequestEntity,
        ResponseEntity,
        PostResponseStubber<ResponseEntity>,
        PostRequestStubber<RequestEntity, ResponseEntity>
        >
{
    public PostRequestStubber(
            WireMockServer wireMockServer,
            ObjectMapper objectMapper,
            MappingBuilder mappingBuilder
    ) {
        super(wireMockServer, objectMapper, mappingBuilder);
    }

    @Override
    public PostResponseStubber<ResponseEntity> andRespond() {
        return new PostResponseStubber<>(wireMockServer, objectMapper, mappingBuilder);
    }
}
