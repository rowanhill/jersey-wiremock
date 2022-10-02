package io.jerseywiremock.core.verify;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;

public class DeleteRequestVerifier extends EmptyRequestVerifier {
    public DeleteRequestVerifier(
            WireMock wireMock,
            RequestPatternBuilder patternBuilder
    ) {
        super(wireMock, patternBuilder);
    }
}
