package jerseywiremock.core.stub;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ValueMatchingStrategy;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;

public abstract class RequestAndResponseRequestStubber<
        RequestEntity,
        ResponseEntity,
        ResponseStubber extends SimpleEntityResponseStubber<ResponseEntity, ResponseStubber>,
        Self extends RequestAndResponseRequestStubber<RequestEntity, ResponseEntity, ResponseStubber, Self>
        > extends EmptyRequestSimpleResponseRequestStubber<ResponseEntity, ResponseStubber> {
    public RequestAndResponseRequestStubber(
            WireMockServer wireMockServer,
            ObjectMapper objectMapper,
            MappingBuilder mappingBuilder
    ) {
        super(wireMockServer, objectMapper, mappingBuilder);
    }

    public Self withRequestEntity(RequestEntity requestEntity)
            throws JsonProcessingException
    {
        String entityString = objectMapper.writeValueAsString(requestEntity);
        mappingBuilder.withRequestBody(equalTo(entityString));
        //noinspection unchecked
        return (Self) this;
    }

    public Self withRequestBody(ValueMatchingStrategy strategy) {
        mappingBuilder.withRequestBody(strategy);
        //noinspection unchecked
        return (Self) this;
    }
}
