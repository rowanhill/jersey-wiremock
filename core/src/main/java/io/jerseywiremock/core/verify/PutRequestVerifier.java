package io.jerseywiremock.core.verify;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;

import io.jerseywiremock.core.stub.request.Serializer;

public class PutRequestVerifier<Entity> extends RequestWithEntityVerifier<Entity, PutRequestVerifier<Entity>> {
    public PutRequestVerifier(
            WireMock wireMock,
            Serializer serializer,
            RequestPatternBuilder patternBuilder
    ) {
        super(wireMock, serializer, patternBuilder);
    }
}
