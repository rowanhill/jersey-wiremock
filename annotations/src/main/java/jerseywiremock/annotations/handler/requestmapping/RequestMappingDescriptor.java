package jerseywiremock.annotations.handler.requestmapping;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.client.UrlMatchingStrategy;
import jerseywiremock.annotations.handler.requestmapping.paramdescriptors.QueryParamMatchDescriptor;
import jerseywiremock.annotations.handler.requestmapping.queryparam.StubOrVerifyQueryParamAdder;
import jerseywiremock.annotations.handler.requestmapping.queryparam.WireMockQueryParamBuilderWrapper;
import jerseywiremock.annotations.handler.requestmapping.stubverbs.VerbMappingBuilderStrategy;
import jerseywiremock.annotations.handler.requestmapping.verifyverbs.VerbRequestedForStrategy;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

public class RequestMappingDescriptor {
    private final String urlPath;
    private final List<QueryParamMatchDescriptor> queryParamMatchDescriptors;

    public RequestMappingDescriptor(String urlPath, List<QueryParamMatchDescriptor> queryParamMatchDescriptors) {
        this.urlPath = urlPath;
        this.queryParamMatchDescriptors = queryParamMatchDescriptors;
    }

    public MappingBuilder toMappingBuilder(VerbMappingBuilderStrategy verbMappingBuilderStrategy) {
        UrlMatchingStrategy urlMatchingStrategy = urlPathEqualTo(urlPath);
        MappingBuilder mappingBuilder = verbMappingBuilderStrategy.verb(urlMatchingStrategy);
        StubOrVerifyQueryParamAdder queryParamAdder =
                new StubOrVerifyQueryParamAdder(new WireMockQueryParamBuilderWrapper(mappingBuilder));
        queryParamAdder.addQueryParameters(queryParamMatchDescriptors);
        return mappingBuilder;
    }

    public RequestPatternBuilder toRequestPatternBuilder(VerbRequestedForStrategy verbRequestedForStrategy) {
        UrlMatchingStrategy urlMatchingStrategy = urlPathEqualTo(urlPath);
        RequestPatternBuilder patternBuilder = verbRequestedForStrategy.verbRequestedFor(urlMatchingStrategy);
        StubOrVerifyQueryParamAdder queryParamAdder =
                new StubOrVerifyQueryParamAdder(new WireMockQueryParamBuilderWrapper(patternBuilder));
        queryParamAdder.addQueryParameters(queryParamMatchDescriptors);
        return patternBuilder;
    }
}
