package io.jerseywiremock.core.verify;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;

public abstract class BaseRequestVerifier<Self extends BaseRequestVerifier> {
    protected final WireMock wireMock;
    protected final RequestPatternBuilder requestPatternBuilder;

    private Integer numOfTimes;

    public BaseRequestVerifier(WireMock wireMock, RequestPatternBuilder patternBuilder) {
        this.wireMock = wireMock;
        this.requestPatternBuilder = patternBuilder;
    }

    public Self times(int numTimes) {
        this.numOfTimes = numTimes;
        //noinspection unchecked
        return (Self) this;
    }

    public void verify() {
        if (numOfTimes != null) {
            wireMock.verifyThat(numOfTimes, requestPatternBuilder);
        } else {
            wireMock.verifyThat(requestPatternBuilder);
        }
    }
}
