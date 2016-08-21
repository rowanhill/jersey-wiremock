package io.jerseywiremock.core.verify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.RequestPatternBuilder;

public class PostRequestVerifier<Entity> extends RequestWithEntityVerifier<Entity, PostRequestVerifier<Entity>> {
    public PostRequestVerifier(
            WireMockServer wireMockServer,
            ObjectMapper objectMapper,
            RequestPatternBuilder patternBuilder
    ) {
        super(wireMockServer, objectMapper, patternBuilder);
    }
}
