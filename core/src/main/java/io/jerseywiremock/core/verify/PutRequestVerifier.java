package io.jerseywiremock.core.verify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.RequestPatternBuilder;

public class PutRequestVerifier<Entity> extends RequestWithEntityVerifier<Entity, PutRequestVerifier<Entity>> {
    public PutRequestVerifier(
            WireMockServer wireMockServer,
            ObjectMapper objectMapper,
            RequestPatternBuilder patternBuilder
    ) {
        super(wireMockServer, objectMapper, patternBuilder);
    }
}
