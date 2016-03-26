package jerseywiremock.core.stub;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ValueMatchingStrategy;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;

public class RequestAndResponseRequestStubber<RequestEntity, ResponseEntity> extends BaseRequestStubber {
    public RequestAndResponseRequestStubber(
            WireMockServer wireMockServer,
            ObjectMapper objectMapper,
            MappingBuilder mappingBuilder
    ) {
        super(wireMockServer, objectMapper, mappingBuilder);
    }

    public RequestAndResponseRequestStubber<RequestEntity, ResponseEntity> withRequestEntity(RequestEntity requestEntity)
            throws JsonProcessingException
    {
        String entityString = objectMapper.writeValueAsString(requestEntity);
        mappingBuilder.withRequestBody(equalTo(entityString));
        return this;
    }

    public RequestAndResponseRequestStubber<RequestEntity, ResponseEntity> withRequestBody(ValueMatchingStrategy strategy) {
        mappingBuilder.withRequestBody(strategy);
        return this;
    }

    public SimpleEntityResponseStubber<ResponseEntity> andRespond() {
        return new SimpleEntityResponseStubber<>(wireMockServer, objectMapper, mappingBuilder);
    }

    public SimpleEntityResponseStubber<ResponseEntity> andRespondWith(ResponseEntity responseEntity) {
        return andRespond().withEntity(responseEntity);
    }
}