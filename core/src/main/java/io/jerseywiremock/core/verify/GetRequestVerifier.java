package io.jerseywiremock.core.verify;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.RequestPatternBuilder;

public class GetRequestVerifier extends EmptyRequestVerifier {
    public GetRequestVerifier(
            WireMockServer wireMockServer,
            RequestPatternBuilder patternBuilder
    ) {
        super(wireMockServer, patternBuilder);
    }
}
