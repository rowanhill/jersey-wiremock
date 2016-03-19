package jerseywiremock.core.verify;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.client.UrlMatchingStrategy;
import jerseywiremock.core.RequestMappingDescriptor;
import jerseywiremock.core.StubOrVerifyQueryParamAdder;

import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

public abstract class BaseRequestVerifyBuilder {
    private final WireMockServer wireMockServer;
    private final RequestPatternBuilder requestPatternBuilder;

    private Integer numOfTimes;

    public BaseRequestVerifyBuilder(WireMockServer wireMockServer, RequestPatternBuilder patternBuilder) {
        this.wireMockServer = wireMockServer;
        this.requestPatternBuilder = patternBuilder;
    }

    public BaseRequestVerifyBuilder(
            WireMockServer wireMockServer,
            VerbRequestedForStrategy verbRequestedForStrategy,
            RequestMappingDescriptor mappingDescriptor
    ) {
        this(wireMockServer, createRequestPatternBuilder(verbRequestedForStrategy, mappingDescriptor));
    }

    public BaseRequestVerifyBuilder times(int numTimes) {
        this.numOfTimes = numTimes;
        return this;
    }

    public void verify() {
        if (numOfTimes != null) {
            wireMockServer.verify(numOfTimes, requestPatternBuilder);
        } else {
            wireMockServer.verify(requestPatternBuilder);
        }
    }

    private static RequestPatternBuilder createRequestPatternBuilder(
            VerbRequestedForStrategy verbRequestedForStrategy,
            RequestMappingDescriptor mappingDescriptor
    ) {
        UrlMatchingStrategy urlMatchingStrategy = urlPathEqualTo(mappingDescriptor.getUrlPath());
        RequestPatternBuilder patternBuilder = verbRequestedForStrategy.verbRequestedFor(urlMatchingStrategy);
        StubOrVerifyQueryParamAdder queryParamAdder = new StubOrVerifyQueryParamAdder(patternBuilder);
        queryParamAdder.addQueryParameters(mappingDescriptor);
        return patternBuilder;
    }
}
