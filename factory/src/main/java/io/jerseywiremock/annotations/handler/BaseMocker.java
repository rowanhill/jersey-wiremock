package io.jerseywiremock.annotations.handler;

import com.github.tomakehurst.wiremock.client.WireMock;

import io.jerseywiremock.core.stub.request.Serializers;

public abstract class BaseMocker {
    protected final WireMock wireMock;
    protected final Serializers serializers;

    public BaseMocker(WireMock wireMock, Serializers serializers) {
        this.wireMock = wireMock;
        this.serializers = serializers;
    }
}
