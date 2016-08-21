package io.jerseywiremock.core.verify;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.client.ValueMatchingStrategy;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;

public abstract class RequestWithEntityVerifier<Entity, Self extends RequestWithEntityVerifier<Entity, Self>>
        extends BaseRequestVerifier<RequestWithEntityVerifier>
{
    protected ObjectMapper objectMapper;

    public RequestWithEntityVerifier(
            WireMockServer wireMockServer,
            ObjectMapper objectMapper,
            RequestPatternBuilder patternBuilder
    ) {
        super(wireMockServer, patternBuilder);
        this.objectMapper = objectMapper;
    }

    public Self withRequestEntity(Entity entity) throws JsonProcessingException {
        String entityString = objectMapper.writeValueAsString(entity);
        requestPatternBuilder.withRequestBody(equalTo(entityString));
        //noinspection unchecked
        return (Self) this;
    }

    public Self withRequestBody(ValueMatchingStrategy valueMatchingStrategy) {
        requestPatternBuilder.withRequestBody(valueMatchingStrategy);
        //noinspection unchecked
        return (Self) this;
    }
}
