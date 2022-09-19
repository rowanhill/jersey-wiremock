package io.jerseywiremock.annotations.factory;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.JacksonSerializer;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

import io.jerseywiremock.annotations.WireMockForResource;
import io.jerseywiremock.annotations.WireMockStub;
import io.jerseywiremock.annotations.WireMockVerify;
import io.jerseywiremock.annotations.handler.BaseMocker;
import io.jerseywiremock.core.stub.request.GetSingleRequestStubber;
import io.jerseywiremock.core.stub.request.Serializers;
import io.jerseywiremock.core.verify.GetRequestVerifier;

public class MockerFactoryAbstractClassTest {
    private TestClient client;
    private TestMockerFromBase mocker;
    private final WireMockServer wireMockServer = new WireMockServer(8080);

    @BeforeEach
    public void setUp() throws Exception {
        wireMockServer.start();
        client = new TestClient();
        Serializers serializers = new Serializers();
        serializers.addSerializer("application/json", new JacksonSerializer());
        mocker = MockerFactory.wireMockerFor(TestMockerFromBase.class, new WireMock(8080), serializers);
    }

    @AfterEach
    void after() {
        wireMockServer.stop();
    }

    @Test
    public void abstractMethodsCanBeCalled() throws Exception {
        // given
        mocker.stubGetByQuery(1).andRespondWith(10).stub();

        // when
        int result = client.getByQuery(1);

        // then
        assertThat(result).isEqualTo(10);
        mocker.verifyGetByQuery(1).times(1).verify();
    }

    @Test
    public void handCraftedMethodsCanBeCalled() {
        // given
        mocker.stub500ForAnyUrlStartingBadUrl();

        // when
        Response response = client.makeBadRequest();

        // then
        assertThat(response.getStatus()).isEqualTo(500);
    }

    @Test
    public void exceptionIsThrownCreatingMockerFromAbstractClassThatDoesNotInheritFromBaseMocker() {
        Serializers serializers = new Serializers();
        serializers.addSerializer("application/json", new JacksonSerializer());
        assertThrows(Exception.class, () -> MockerFactory.wireMockerFor(TestMockerWithoutBase.class, new WireMock(8080), serializers));
    }

    @WireMockForResource(TestResource.class)
    public static abstract class TestMockerFromBase extends BaseMocker {
        public TestMockerFromBase(
                WireMock wireMock,
                Serializers serializers
        ) {
            super(wireMock, serializers);
        }

        public void stub500ForAnyUrlStartingBadUrl() {
            wireMock.register(get(urlPathMatching("/bad-url(-.+)?"))
                    .willReturn(aResponse().withStatus(500)));
        }

        @WireMockStub("getByQuery")
        public abstract GetSingleRequestStubber<Integer> stubGetByQuery(int input);

        @WireMockVerify("getByQuery")
        public abstract GetRequestVerifier verifyGetByQuery(int input);
    }

    public static abstract class TestMockerWithoutBase {
    }

    @Path("/test")
    public static class TestResource {
        @GET
        @Path("byQuery")
        public int getByQuery(@QueryParam("input") int input) {
            return 12345;
        }
    }

    public static class TestClient {
        private final Client client = ClientBuilder.newClient().register(JacksonJsonProvider.class);

        public int getByQuery(int input) {
            return client
                    .target(UriBuilder
                            .fromResource(TestResource.class)
                            .path(TestResource.class, "getByQuery")
                            .queryParam("input", Integer.toString(input))
                            .scheme("http")
                            .host("localhost")
                            .port(8080))
                    .request()
                    .get()
                    .readEntity(Integer.class);
        }

        public Response makeBadRequest() {
            return client.target(UriBuilder.fromPath("/bad-url-test").scheme("http").host("localhost").port(8080))
                    .request()
                    .get();
        }
    }
}