package jerseywiremock.core.stub;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ValueMatchingStrategy;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;

public class PostRequestStubber<RequestEntity, ResponseEntity> extends BaseRequestStubber {
    public PostRequestStubber(
            WireMockServer wireMockServer,
            ObjectMapper objectMapper,
            MappingBuilder mappingBuilder
    ) {
        super(wireMockServer, objectMapper, mappingBuilder);
    }

    public PostRequestStubber<RequestEntity, ResponseEntity> withRequestEntity(RequestEntity requestEntity)
            throws JsonProcessingException
    {
        String entityString = objectMapper.writeValueAsString(requestEntity);
        mappingBuilder.withRequestBody(equalTo(entityString));
        return this;
    }

    public PostRequestStubber<RequestEntity, ResponseEntity> withRequestBody(ValueMatchingStrategy strategy) {
        mappingBuilder.withRequestBody(strategy);
        return this;
    }

    public PostResponseStubber<ResponseEntity> andRespond() {
        return new PostResponseStubber<>(wireMockServer, objectMapper, mappingBuilder);
    }

    public PostResponseStubber<ResponseEntity> andRespondWith(ResponseEntity responseEntity) {
        return andRespond().withEntity(responseEntity);
    }
}
