package io.jerseywiremock.annotations.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;

public abstract class BaseMocker {
    protected final WireMockServer wireMockServer;
    protected final ObjectMapper objectMapper;

    public BaseMocker(WireMockServer wireMockServer, ObjectMapper objectMapper) {
        this.wireMockServer = wireMockServer;
        this.objectMapper = objectMapper;
    }
}
