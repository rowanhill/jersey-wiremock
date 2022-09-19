package io.jerseywiremock.core.stub.response;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Fault;

import io.jerseywiremock.core.stub.request.Serializer;

public abstract class BaseResponseStubber<SelfType extends BaseResponseStubber> {
    protected final WireMock wireMock;
    protected final Serializer serializer;
    protected final MappingBuilder mappingBuilder;
    protected final ResponseDefinitionBuilder responseDefinitionBuilder;

    public BaseResponseStubber(
            WireMock wireMock,
            Serializer serializer,
            MappingBuilder mappingBuilder,
            ResponseDefinitionBuilder responseDefinitionBuilder
    ) {
        this.wireMock = wireMock;
        this.serializer = serializer;
        this.mappingBuilder = mappingBuilder;

        if (responseDefinitionBuilder != null) {
            this.responseDefinitionBuilder = responseDefinitionBuilder;
        } else {
            this.responseDefinitionBuilder = aResponse().withHeader("Content-Type", "application/json");
        }
    }

    public SelfType withStatusCode(int statusCode) {
        responseDefinitionBuilder.withStatus(statusCode);
        //noinspection unchecked
        return (SelfType) this;
    }

    public SelfType withFault(Fault fault) {
        responseDefinitionBuilder.withFault(fault);
        //noinspection unchecked
        return (SelfType) this;
    }

    public void stub() throws JsonProcessingException {
        amendResponseDefinition(responseDefinitionBuilder);
        wireMock.register(mappingBuilder.willReturn(responseDefinitionBuilder));
    }

    protected abstract void amendResponseDefinition(ResponseDefinitionBuilder responseDefinitionBuilder) throws JsonProcessingException;
}
