package io.jerseywiremock.annotations.handler.requestmatching.stubverbs;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.UrlMatchingStrategy;

import static com.github.tomakehurst.wiremock.client.WireMock.get;

public class GetMappingBuilderStrategy implements VerbMappingBuilderStrategy {
    public MappingBuilder verb(UrlMatchingStrategy urlMatchingStrategy) {
        return get(urlMatchingStrategy);
    }
}
