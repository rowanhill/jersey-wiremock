package io.jerseywiremock.annotations.handler.requestmatching.verifyverbs;

import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;

import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.matching.UrlPathPattern;

public class GetRequestedForStrategy implements VerbRequestedForStrategy {
    public RequestPatternBuilder verbRequestedFor(UrlPathPattern urlMatchingStrategy) {
        return getRequestedFor(urlMatchingStrategy);
    }
}
