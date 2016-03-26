package jerseywiremock.annotations.handler.requestmapping;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.client.UrlMatchingStrategy;
import jerseywiremock.annotations.handler.requestmapping.paramdescriptors.QueryParamMatchDescriptor;
import jerseywiremock.annotations.handler.requestmapping.paramdescriptors.ValueMatchDescriptor;
import jerseywiremock.annotations.handler.requestmapping.queryparam.StubOrVerifyQueryParamAdder;
import jerseywiremock.annotations.handler.requestmapping.queryparam.WireMockQueryParamBuilderWrapper;
import jerseywiremock.annotations.handler.requestmapping.stubverbs.VerbMappingBuilderStrategy;
import jerseywiremock.annotations.handler.requestmapping.verifyverbs.VerbRequestedForStrategy;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

public class RequestMappingDescriptor {
    private final String urlPath;
    private final List<QueryParamMatchDescriptor> queryParamMatchDescriptors;
    private final ValueMatchDescriptor requestBodyMatchDescriptor;

    public RequestMappingDescriptor(
            String urlPath,
            List<QueryParamMatchDescriptor> queryParamMatchDescriptors,
            ValueMatchDescriptor requestBodyMatchDescriptor
    ) {
        this.urlPath = urlPath;
        this.queryParamMatchDescriptors = queryParamMatchDescriptors;
        this.requestBodyMatchDescriptor = requestBodyMatchDescriptor;
    }

    public MappingBuilder toMappingBuilder(VerbMappingBuilderStrategy verbMappingBuilderStrategy) {
        UrlMatchingStrategy urlMatchingStrategy = urlPathEqualTo(urlPath);
        MappingBuilder mappingBuilder = verbMappingBuilderStrategy.verb(urlMatchingStrategy);
        StubOrVerifyQueryParamAdder queryParamAdder =
                new StubOrVerifyQueryParamAdder(new WireMockQueryParamBuilderWrapper(mappingBuilder));
        queryParamAdder.addQueryParameters(queryParamMatchDescriptors);
        if (requestBodyMatchDescriptor != null) {
            mappingBuilder.withRequestBody(requestBodyMatchDescriptor.toValueMatchingStrategy());
        }
        return mappingBuilder;
    }

    public RequestPatternBuilder toRequestPatternBuilder(VerbRequestedForStrategy verbRequestedForStrategy) {
        UrlMatchingStrategy urlMatchingStrategy = urlPathEqualTo(urlPath);
        RequestPatternBuilder patternBuilder = verbRequestedForStrategy.verbRequestedFor(urlMatchingStrategy);
        StubOrVerifyQueryParamAdder queryParamAdder =
                new StubOrVerifyQueryParamAdder(new WireMockQueryParamBuilderWrapper(patternBuilder));
        queryParamAdder.addQueryParameters(queryParamMatchDescriptors);
        if (requestBodyMatchDescriptor != null) {
            patternBuilder.withRequestBody(requestBodyMatchDescriptor.toValueMatchingStrategy());
        }
        return patternBuilder;
    }
}
