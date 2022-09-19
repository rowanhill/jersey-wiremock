package io.jerseywiremock.annotations.handler.requestmatching.verifyverbs;

import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.matching.UrlPathPattern;

import static com.github.tomakehurst.wiremock.client.WireMock.deleteRequestedFor;

public class DeleteRequestedForStrategy implements VerbRequestedForStrategy {
    @Override
    public RequestPatternBuilder verbRequestedFor(UrlPathPattern urlPathPattern) {
        return deleteRequestedFor(urlPathPattern);
    }
}
