package jerseywiremock.core.verify;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.client.UrlMatchingStrategy;
import jerseywiremock.core.RequestMappingDescriptor;
import jerseywiremock.core.StubOrVerifyQueryParamAdder;
import jerseywiremock.core.WireMockQueryParamBuilderWrapper;

import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

public abstract class BaseRequestVerifyBuilder<Self extends BaseRequestVerifyBuilder> {
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

    public Self times(int numTimes) {
        this.numOfTimes = numTimes;
        //noinspection unchecked
        return (Self) this;
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
        StubOrVerifyQueryParamAdder queryParamAdder =
                new StubOrVerifyQueryParamAdder(new WireMockQueryParamBuilderWrapper(patternBuilder));
        queryParamAdder.addQueryParameters(mappingDescriptor.getQueryParamMatchDescriptors());
        return patternBuilder;
    }
}
