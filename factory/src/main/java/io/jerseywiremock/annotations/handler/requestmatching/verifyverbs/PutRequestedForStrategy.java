package io.jerseywiremock.annotations.handler.requestmatching.verifyverbs;

import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;

import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.matching.UrlPathPattern;

public class PutRequestedForStrategy implements VerbRequestedForStrategy {
    @Override
    public RequestPatternBuilder verbRequestedFor(UrlPathPattern urlPathPattern) {
        return putRequestedFor(urlPathPattern);
    }
}
