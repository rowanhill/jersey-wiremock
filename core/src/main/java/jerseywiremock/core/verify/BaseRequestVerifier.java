package jerseywiremock.core.verify;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.RequestPatternBuilder;

public abstract class BaseRequestVerifier<Self extends BaseRequestVerifier> {
    protected final WireMockServer wireMockServer;
    protected final RequestPatternBuilder requestPatternBuilder;

    private Integer numOfTimes;

    public BaseRequestVerifier(WireMockServer wireMockServer, RequestPatternBuilder patternBuilder) {
        this.wireMockServer = wireMockServer;
        this.requestPatternBuilder = patternBuilder;
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
}
