package jerseywiremock.core.verify;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.RequestPatternBuilder;

import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

public abstract class BaseRequestVerifyBuilder {
    private final WireMockServer wireMockServer;
    private final RequestPatternBuilder requestPatternBuilder;

    private Integer numOfTimes;

    public BaseRequestVerifyBuilder(
            WireMockServer wireMockServer,
            VerbRequestedForStrategy verbRequestedForStrategy,
            String urlPath
    ) {
        this.wireMockServer = wireMockServer;

        this.requestPatternBuilder = verbRequestedForStrategy.verbRequestedFor(urlPathEqualTo(urlPath));
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
}
