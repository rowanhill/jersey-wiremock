package io.jerseywiremock.annotations.handler.requestmatching;

import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.notMatching;

import com.github.tomakehurst.wiremock.matching.StringValuePattern;

import io.jerseywiremock.annotations.ParamMatchingStrategy;

public class ValueMatchingStrategyFactory {
    public StringValuePattern createValueMatchingStrategy(ParamMatchingStrategy matchingStrategy, String value) {
        switch (matchingStrategy) {
            case EQUAL_TO:
                return equalTo(value);
            case CONTAINING:
                return containing(value);
            case MATCHING:
                return matching(value);
            case NOT_MATCHING:
                return notMatching(value);
            default:
                throw new RuntimeException("Unexpected matching strategy " + matchingStrategy.name());
        }
    }
}
