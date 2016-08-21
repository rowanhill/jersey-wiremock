package io.jerseywiremock.annotations.handler.requestmatching.stubverbs;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.UrlMatchingStrategy;

import static com.github.tomakehurst.wiremock.client.WireMock.post;

public class PostMappingBuilderStrategy implements VerbMappingBuilderStrategy {
    @Override
    public MappingBuilder verb(UrlMatchingStrategy urlMatchingStrategy) {
        return post(urlMatchingStrategy);
    }
}
