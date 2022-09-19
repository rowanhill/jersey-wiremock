package io.jerseywiremock.annotations.handler.requestmatching.stubverbs;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.matching.UrlPathPattern;

import static com.github.tomakehurst.wiremock.client.WireMock.get;

public class GetMappingBuilderStrategy implements VerbMappingBuilderStrategy {
    public MappingBuilder verb(UrlPathPattern urlPathPattern) {
        return get(urlPathPattern);
    }
}
