package io.jerseywiremock.core.verify;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.RequestPatternBuilder;

public abstract class EmptyRequestVerifier extends BaseRequestVerifier<EmptyRequestVerifier> {
    public EmptyRequestVerifier(WireMockServer wireMockServer, RequestPatternBuilder patternBuilder) {
        super(wireMockServer, patternBuilder);
    }
}
