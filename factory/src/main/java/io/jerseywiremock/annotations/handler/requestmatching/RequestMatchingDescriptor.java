package io.jerseywiremock.annotations.handler.requestmatching;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.client.UrlMatchingStrategy;
import com.github.tomakehurst.wiremock.client.ValueMatchingStrategy;
import com.google.common.collect.ListMultimap;
import io.jerseywiremock.annotations.handler.requestmatching.stubverbs.VerbMappingBuilderStrategy;
import io.jerseywiremock.annotations.handler.requestmatching.verifyverbs.VerbRequestedForStrategy;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

public class RequestMatchingDescriptor {
    private final String urlPath;
    private final ListMultimap<String, ValueMatchingStrategy> queryParamMatchingStrategies;

    RequestMatchingDescriptor(
            String urlPath,
            ListMultimap<String, ValueMatchingStrategy> queryParamMatchingStrategies
    ) {
        this.urlPath = urlPath;
        this.queryParamMatchingStrategies = queryParamMatchingStrategies;
    }

    public MappingBuilder toMappingBuilder(VerbMappingBuilderStrategy verbMappingBuilderStrategy) {
        UrlMatchingStrategy urlMatchingStrategy = urlPathEqualTo(urlPath);
        MappingBuilder mappingBuilder = verbMappingBuilderStrategy.verb(urlMatchingStrategy);
        for (Map.Entry<String, ValueMatchingStrategy> entry : queryParamMatchingStrategies.entries()) {
            mappingBuilder.withQueryParam(entry.getKey(), entry.getValue());
        }
        return mappingBuilder;
    }

    public RequestPatternBuilder toRequestPatternBuilder(VerbRequestedForStrategy verbRequestedForStrategy) {
        UrlMatchingStrategy urlMatchingStrategy = urlPathEqualTo(urlPath);
        RequestPatternBuilder patternBuilder = verbRequestedForStrategy.verbRequestedFor(urlMatchingStrategy);
        for (Map.Entry<String, ValueMatchingStrategy> entry : queryParamMatchingStrategies.entries()) {
            patternBuilder.withQueryParam(entry.getKey(), entry.getValue());
        }
        return patternBuilder;
    }
}
