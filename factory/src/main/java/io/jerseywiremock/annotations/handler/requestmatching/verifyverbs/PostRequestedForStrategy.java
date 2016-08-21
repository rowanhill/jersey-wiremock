package io.jerseywiremock.annotations.handler.requestmatching.verifyverbs;

import com.github.tomakehurst.wiremock.client.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.client.UrlMatchingStrategy;

import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;

public class PostRequestedForStrategy implements VerbRequestedForStrategy {
    @Override
    public RequestPatternBuilder verbRequestedFor(UrlMatchingStrategy urlMatchingStrategy) {
        return postRequestedFor(urlMatchingStrategy);
    }
}
