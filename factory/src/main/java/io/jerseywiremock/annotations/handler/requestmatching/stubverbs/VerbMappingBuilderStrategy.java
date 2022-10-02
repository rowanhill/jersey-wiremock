package io.jerseywiremock.annotations.handler.requestmatching.stubverbs;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.matching.UrlPathPattern;

public interface VerbMappingBuilderStrategy {
    MappingBuilder verb(UrlPathPattern urlPathPattern);
}
