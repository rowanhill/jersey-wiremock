package io.jerseywiremock.core.stub.request;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;

import io.jerseywiremock.core.stub.response.BaseResponseStubber;

public abstract class BaseRequestStubber<ResponseStubber extends BaseResponseStubber<ResponseStubber>> {
    protected final WireMock wireMock;
    protected final Serializer serializer;
    protected final MappingBuilder mappingBuilder;
    protected final ResponseDefinitionBuilder responseDefinitionBuilder;

    public BaseRequestStubber(
            WireMock wireMock,
            Serializer serializer,
            MappingBuilder mappingBuilder,
            ResponseDefinitionBuilder responseDefinitionBuilder
    ) {
        this.wireMock = wireMock;
        this.serializer = serializer;
        this.mappingBuilder = mappingBuilder;
        this.responseDefinitionBuilder = responseDefinitionBuilder;
    }

    public BaseRequestStubber(WireMock wireMock, Serializer serializer, MappingBuilder mappingBuilder) {
        this(wireMock, serializer, mappingBuilder, null);
    }

    public abstract ResponseStubber andRespond();
}
