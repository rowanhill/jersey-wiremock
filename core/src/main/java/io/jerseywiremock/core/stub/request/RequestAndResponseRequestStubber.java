package io.jerseywiremock.core.stub.request;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.ContentPattern;

import io.jerseywiremock.core.stub.response.SimpleEntityResponseStubber;

public abstract class RequestAndResponseRequestStubber<
        RequestEntity,
        ResponseEntity,
        ResponseStubber extends SimpleEntityResponseStubber<ResponseEntity, ResponseStubber>,
        Self extends RequestAndResponseRequestStubber<RequestEntity, ResponseEntity, ResponseStubber, Self>
        > extends EmptyRequestSimpleResponseRequestStubber<ResponseEntity, ResponseStubber>
{
    public RequestAndResponseRequestStubber(
            WireMock wireMock,
            Serializer serializer,
            MappingBuilder mappingBuilder,
            ResponseDefinitionBuilder responseDefinitionBuilder
    ) {
        super(wireMock, serializer, mappingBuilder, responseDefinitionBuilder);
    }

    public RequestAndResponseRequestStubber(
            WireMock wireMock,
            Serializer serializer,
            MappingBuilder mappingBuilder
    ) {
        super(wireMock, serializer, mappingBuilder);
    }

    public Self withRequestEntity(RequestEntity requestEntity) {
        String entityString = serializer.serialize(requestEntity);
        mappingBuilder.withRequestBody(equalTo(entityString));
        //noinspection unchecked
        return (Self) this;
    }

    public Self withRequestBody(ContentPattern<?> strategy) {
        mappingBuilder.withRequestBody(strategy);
        //noinspection unchecked
        return (Self) this;
    }
}
