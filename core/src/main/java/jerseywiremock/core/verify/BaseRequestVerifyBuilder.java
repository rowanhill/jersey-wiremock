package jerseywiremock.core.verify;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.client.UrlMatchingStrategy;
import jerseywiremock.core.RequestMappingDescriptor;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

public abstract class BaseRequestVerifyBuilder {
    private final WireMockServer wireMockServer;
    private final RequestPatternBuilder requestPatternBuilder;

    private Integer numOfTimes;

    public BaseRequestVerifyBuilder(
            WireMockServer wireMockServer,
            VerbRequestedForStrategy verbRequestedForStrategy,
            RequestMappingDescriptor mappingDescriptor
    ) {
        this.wireMockServer = wireMockServer;

        this.requestPatternBuilder = createRequestPatternBuilder(verbRequestedForStrategy, mappingDescriptor);
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

    private RequestPatternBuilder createRequestPatternBuilder(
            VerbRequestedForStrategy verbRequestedForStrategy,
            RequestMappingDescriptor mappingDescriptor
    ) {
        UrlMatchingStrategy urlMatchingStrategy = urlPathEqualTo(mappingDescriptor.getUrlPath());
        RequestPatternBuilder patternBuilder = verbRequestedForStrategy.verbRequestedFor(urlMatchingStrategy);
        for (Map.Entry<String, String> entry : mappingDescriptor.getQueryParams().entrySet()) {
            patternBuilder.withQueryParam(entry.getKey(), equalTo(entry.getValue()));
        }
        return patternBuilder;
    }
}
