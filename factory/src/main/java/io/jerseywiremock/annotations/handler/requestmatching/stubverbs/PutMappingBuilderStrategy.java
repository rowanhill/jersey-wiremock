package io.jerseywiremock.annotations.handler.requestmatching.stubverbs;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.matching.UrlPathPattern;

import static com.github.tomakehurst.wiremock.client.WireMock.put;

public class PutMappingBuilderStrategy implements VerbMappingBuilderStrategy {
    @Override
    public MappingBuilder verb(UrlPathPattern urlPathPattern) {
        return put(urlPathPattern);
    }
}
