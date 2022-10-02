package io.jerseywiremock.core.verify;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;

public class GetRequestVerifier extends EmptyRequestVerifier {
    public GetRequestVerifier(
            WireMock wireMock,
            RequestPatternBuilder patternBuilder
    ) {
        super(wireMock, patternBuilder);
    }
}
