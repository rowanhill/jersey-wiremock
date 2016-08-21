package io.jerseywiremock.annotations.handler.requestmatching.verifyverbs;

import com.github.tomakehurst.wiremock.client.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.client.UrlMatchingStrategy;

import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;

public class GetRequestedForStrategy implements VerbRequestedForStrategy {
    public RequestPatternBuilder verbRequestedFor(UrlMatchingStrategy urlMatchingStrategy) {
        return getRequestedFor(urlMatchingStrategy);
    }
}
