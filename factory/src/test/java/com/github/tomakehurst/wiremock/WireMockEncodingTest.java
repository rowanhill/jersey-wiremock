package com.github.tomakehurst.wiremock;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Rule;
import org.junit.Test;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

public class WireMockEncodingTest {
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8080);

    @Test
    public void queryParamsMustBeEncodedWhenStubbingButDecodedWhenVerifying() {
        // given
        String value = "a:b";
        String encodedValue = "a%3Ab";
        wireMockRule.stubFor(get(urlPathEqualTo("/test"))
                .withQueryParam("q", equalTo(encodedValue))
                .willReturn(aResponse()));

        // when
        Response response = ClientBuilder.newClient()
                .target("http://localhost:8080/test").queryParam("q", value)
                .request()
                .get();

        // then
        wireMockRule.verify(getRequestedFor(urlPathEqualTo("/test"))
                .withQueryParam("q", equalTo(value)));
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    public void usingOnlyDecodedQueryParamValueWhenBothStubbingAndVerifyingCausesRequestsNotToMatch() {
        // given
        String value = "a:b";
        wireMockRule.stubFor(get(urlPathEqualTo("/test"))
                .withQueryParam("q", equalTo(value))
                .willReturn(aResponse()));

        // when
        Response response = ClientBuilder.newClient()
                .target("http://localhost:8080/test").queryParam("q", value)
                .request()
                .get();

        // then
        wireMockRule.verify(getRequestedFor(urlPathEqualTo("/test"))
                .withQueryParam("q", equalTo(value)));
        assertThat(response.getStatus()).isEqualTo(404);
    }

    @Test
    public void usingOnlyEncodedQueryParamValueWhenBothStubbingAndVerifyingCausesVerifyToNotMatch() {
        // given
        String value = "a:b";
        String encodedValue = "a%3Ab";
        wireMockRule.stubFor(get(urlPathEqualTo("/test"))
                .withQueryParam("q", equalTo(encodedValue))
                .willReturn(aResponse()));

        // when
        Response response = ClientBuilder.newClient()
                .target("http://localhost:8080/test").queryParam("q", value)
                .request()
                .get();

        // then
        wireMockRule.verify(0, getRequestedFor(urlPathEqualTo("/test"))
                .withQueryParam("q", equalTo(encodedValue)));
        assertThat(response.getStatus()).isEqualTo(200);
    }
}
