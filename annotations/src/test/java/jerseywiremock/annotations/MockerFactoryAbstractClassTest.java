package jerseywiremock.annotations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import jerseywiremock.annotations.handler.BaseMocker;
import jerseywiremock.core.stub.GetRequestMocker;
import jerseywiremock.core.verify.GetRequestVerifier;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

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
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8080);

    private TestClient client;
    private TestMockerFromBase mocker;

    @Before
    public void setUp() throws Exception {
        client = new TestClient();
        ObjectMapper objectMapper = new ObjectMapper();
        mocker = MockerFactory.wireMockerFor(TestMockerFromBase.class, wireMockRule, objectMapper);
    }

    @Test
    public void abstractMethodsCanBeCalled() throws Exception {
        // given
        mocker.stubGetByQuery(1).andRespondWith(10).stub();

        // when
        client.getByQuery(1);

        // then
        mocker.verifyGetByQuery(1).times(1).verify();
    }

    @Test
    public void handCraftedMethodsCanBeCalled() throws Exception {
        // given
        mocker.stub500ForAnyUrlStartingBadUrl();

        // when
        System.out.println(wireMockRule.listAllStubMappings().getMappings());
        Response response = client.makeBadRequest();

        // then
        assertThat(response.getStatus()).isEqualTo(500);
    }

    @WireMockForResource(TestResource.class)
    public static abstract class TestMockerFromBase extends BaseMocker {
        public TestMockerFromBase(
                WireMockServer wireMockServer,
                ObjectMapper objectMapper
        ) {
            super(wireMockServer, objectMapper);
        }

        public void stub500ForAnyUrlStartingBadUrl() {
            wireMockServer.stubFor(get(urlPathMatching("/bad-url(-.+)?"))
                    .willReturn(aResponse().withStatus(500)));
        }

        @WireMockStub("getByQuery")
        public abstract GetRequestMocker<Integer> stubGetByQuery(int input);

        @WireMockVerify("getByQuery")
        public abstract GetRequestVerifier verifyGetByQuery(int input);
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
        private final Client client = ClientBuilder.newClient().register(new JacksonJaxbJsonProvider());

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