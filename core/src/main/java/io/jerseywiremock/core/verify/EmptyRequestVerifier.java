package io.jerseywiremock.core.verify;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;

public abstract class EmptyRequestVerifier extends BaseRequestVerifier<EmptyRequestVerifier> {
    public EmptyRequestVerifier(WireMock wireMock, RequestPatternBuilder patternBuilder) {
        super(wireMock, patternBuilder);
    }
}
