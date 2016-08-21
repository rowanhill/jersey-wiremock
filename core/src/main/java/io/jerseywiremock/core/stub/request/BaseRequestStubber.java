package io.jerseywiremock.core.stub.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import io.jerseywiremock.core.stub.response.BaseResponseStubber;

public abstract class BaseRequestStubber<ResponseStubber extends BaseResponseStubber<ResponseStubber>> {
    protected final WireMockServer wireMockServer;
    protected final ObjectMapper objectMapper;
    protected final MappingBuilder mappingBuilder;
    protected final ResponseDefinitionBuilder responseDefinitionBuilder;

    public BaseRequestStubber(
            WireMockServer wireMockServer,
            ObjectMapper objectMapper,
            MappingBuilder mappingBuilder,
            ResponseDefinitionBuilder responseDefinitionBuilder
    ) {
        this.wireMockServer = wireMockServer;
        this.objectMapper = objectMapper;
        this.mappingBuilder = mappingBuilder;
        this.responseDefinitionBuilder = responseDefinitionBuilder;
    }

    public BaseRequestStubber(WireMockServer wireMockServer, ObjectMapper objectMapper, MappingBuilder mappingBuilder) {
        this(wireMockServer, objectMapper, mappingBuilder, null);
    }

    public abstract ResponseStubber andRespond();
}
