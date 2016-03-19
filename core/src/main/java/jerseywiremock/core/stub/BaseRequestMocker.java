package jerseywiremock.core.stub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.UrlMatchingStrategy;
import jerseywiremock.core.RequestMappingDescriptor;
import jerseywiremock.core.StubOrVerifyQueryParamAdder;
import jerseywiremock.core.stub.verbs.VerbMappingBuilderStrategy;

import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

public abstract class BaseRequestMocker {
    protected final WireMockServer wireMockServer;
    protected final ObjectMapper objectMapper;
    protected final MappingBuilder mappingBuilder;

    public BaseRequestMocker(
            WireMockServer wireMockServer,
            ObjectMapper objectMapper,
            RequestMappingDescriptor mappingDescriptor,
            VerbMappingBuilderStrategy verbMappingBuilderStrategy
    ) {
        this.wireMockServer = wireMockServer;
        this.objectMapper = objectMapper;

        this.mappingBuilder = createMappingBuilder(verbMappingBuilderStrategy, mappingDescriptor);
    }

    private MappingBuilder createMappingBuilder(
            VerbMappingBuilderStrategy verbMappingBuilderStrategy,
            RequestMappingDescriptor mappingDescriptor
    ) {
        UrlMatchingStrategy urlMatchingStrategy = urlPathEqualTo(mappingDescriptor.getUrlPath());
        MappingBuilder mappingBuilder = verbMappingBuilderStrategy.verb(urlMatchingStrategy);
        StubOrVerifyQueryParamAdder queryParamAdder = new StubOrVerifyQueryParamAdder(mappingBuilder);
        queryParamAdder.addQueryParameters(mappingDescriptor);
        return mappingBuilder;
    }
}
