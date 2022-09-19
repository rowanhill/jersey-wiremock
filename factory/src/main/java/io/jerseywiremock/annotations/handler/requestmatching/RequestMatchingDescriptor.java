package io.jerseywiremock.annotations.handler.requestmatching;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import com.github.tomakehurst.wiremock.matching.UrlPathPattern;
import com.google.common.collect.ListMultimap;
import io.jerseywiremock.annotations.handler.requestmatching.stubverbs.VerbMappingBuilderStrategy;
import io.jerseywiremock.annotations.handler.requestmatching.verifyverbs.VerbRequestedForStrategy;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

public class RequestMatchingDescriptor {
    private final String urlPath;
    private final ListMultimap<String, StringValuePattern> queryParamMatchingStrategies;
    private final ListMultimap<String, StringValuePattern> headerParamMatchingStrategies;

    RequestMatchingDescriptor(
            String urlPath,
            ListMultimap<String, StringValuePattern> queryParamMatchingStrategies,
            ListMultimap<String, StringValuePattern> headerParamMatchingStrategies
    ) {
        this.urlPath = urlPath;
        this.queryParamMatchingStrategies = queryParamMatchingStrategies;
        this.headerParamMatchingStrategies = headerParamMatchingStrategies;
    }

    public MappingBuilder toMappingBuilder(VerbMappingBuilderStrategy verbMappingBuilderStrategy) {
        UrlPathPattern urlMatchingStrategy = urlPathEqualTo(urlPath);
        MappingBuilder mappingBuilder = verbMappingBuilderStrategy.verb(urlMatchingStrategy);
        for (Map.Entry<String, StringValuePattern> entry : queryParamMatchingStrategies.entries()) {
            mappingBuilder.withQueryParam(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, StringValuePattern> entry : headerParamMatchingStrategies.entries()) {
            mappingBuilder.withHeader(entry.getKey(), entry.getValue());
        }
        return mappingBuilder;
    }

    public RequestPatternBuilder toRequestPatternBuilder(VerbRequestedForStrategy verbRequestedForStrategy) {
        UrlPathPattern urlMatchingStrategy = urlPathEqualTo(urlPath);
        RequestPatternBuilder patternBuilder = verbRequestedForStrategy.verbRequestedFor(urlMatchingStrategy);
        for (Map.Entry<String, StringValuePattern> entry : queryParamMatchingStrategies.entries()) {
            patternBuilder.withQueryParam(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, StringValuePattern> entry : headerParamMatchingStrategies.entries()) {
            patternBuilder.withHeader(entry.getKey(), entry.getValue());
        }
        return patternBuilder;
    }
}
