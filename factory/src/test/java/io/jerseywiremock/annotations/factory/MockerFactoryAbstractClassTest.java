package io.jerseywiremock.annotations.factory;

import com.JacksonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import io.jerseywiremock.annotations.WireMockForResource;
import io.jerseywiremock.annotations.WireMockStub;
import io.jerseywiremock.annotations.WireMockVerify;
import io.jerseywiremock.annotations.handler.BaseMocker;
import io.jerseywiremock.core.stub.request.GetSingleRequestStubber;
import io.jerseywiremock.core.stub.request.Serializers;
import io.jerseywiremock.core.verify.GetRequestVerifier;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

public class MockerFactoryAbstractClassTest {
    private static final int WIREMOCK_PORT = 8080;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(WIREMOCK_PORT);

    private TestClient client;
    private TestMockerFromBase mocker;

    @Before
    public void setUp() throws Exception {
        client = new TestClient();
        Serializers serializers = new Serializers();
        serializers.addSerializer("application/json", new JacksonSerializer());
        mocker = MockerFactory.wireMockerFor(TestMockerFromBase.class, new WireMock(8080), serializers);
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
    public void exceptionIsThrownCreatingMockerFromAbstractClassThatDoesNotInheritFromBaseMocker() throws Exception {
        Serializers serializers = new Serializers();
        serializers.addSerializer("application/json", new JacksonSerializer());
        expectedException.expectMessage("must subclass BaseMocker. TestMockerWithoutBase does not.");
        MockerFactory.wireMockerFor(TestMockerWithoutBase.class, new WireMock(8080), serializers);
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