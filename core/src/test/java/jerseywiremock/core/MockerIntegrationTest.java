package jerseywiremock.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.collect.ImmutableList;
import jerseywiremock.core.stub.GetRequestStubber;
import jerseywiremock.core.stub.ListRequestStubber;
import jerseywiremock.core.stub.PostRequestStubber;
import jerseywiremock.core.verify.GetRequestVerifier;
import jerseywiremock.core.verify.PostRequestVerifier;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.util.ArrayList;
import java.util.Collection;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

public class MockerIntegrationTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8080);

    private TestClient client;
    private TestMocker mocker;

    @Before
    public void setUp() throws Exception {
        client = new TestClient();
        ObjectMapper objectMapper = new ObjectMapper();
        mocker = new TestMocker(wireMockRule, objectMapper);
    }

    @Test
    public void getWithPathParamCanBeStubbedAndVerified() throws Exception {
        // given
        mocker.stubGetDoubleGivenInt(1).andRespondWith(5).stub();

        // when
        int doubleOne = client.getDoubleGivenInt(1);

        // then
        assertThat(doubleOne).isEqualTo(5);
        mocker.verifyGetDoubleGivenInt(1).times(1).verify();
    }

    @Test
    public void listWithPathParamCanBeStubbedAndVerified() throws Exception {
        // given
        mocker.stubGetListOfInts().andRespondWith(1, 2, 3).stub();

        // when
        Collection<Integer> listOfInts = client.getListOfInts();

        // then
        assertThat(listOfInts).containsOnly(1, 2, 3);
        mocker.verifyGetListOfInts().times(1).verify();
    }

    @Test
    public void getWithQueryParamCanBeStubbedAndVerified() throws Exception {
        // given
        mocker.stubGetOdds(6).andRespondWith(1, 3, 5).stub();

        // when
        Collection<Integer> odds = client.getOddsLessThan(6);

        // then
        assertThat(odds).containsOnly(1, 3, 5);
        mocker.verifyGetOdds(6).verify();
    }

    @Test
    public void requestCanBeStubbedWithArbitraryStatusCodes() throws Exception {
        // given
        mocker.stubGetListOfInts().andRespond().withStatusCode(403).withEntities(10, 20).stub();

        // when
        Response response = client.getResponseForListOfInts();

        // then
        assertThat(response.getStatus()).isEqualTo(403);
    }

    @Test
    public void postRequestCanBeStubbedAndVerified() throws Exception {
        // given
        mocker.stubPostName().withRequestEntity("Some Name").andRespond().stub();

        // when
        client.postName("Some Name");

        // then
        mocker.verifyPostName().withRequestEntity("Some Name").verify();
    }

    @Test
    public void postRequestCanBeStubbedForArbitraryEntityMatcherAndVerified() throws Exception {
        // given
        mocker.stubPostName().withRequestBody(containing("Some")).andRespond().stub();

        // when
        client.postName("Some Name");

        // then
        mocker.verifyPostName().withRequestBody(containing("Some")).verify();
    }

    @Test
    public void postRequestsCanBeStubbedToReturnEntities() throws Exception {
        // given
        mocker.stubPostName().withRequestEntity("Some Name").andRespondWith(1).stub();

        // when
        int number = client.postName("Some Name");

        // then
        assertThat(number).isEqualTo(1);
    }

    public static class TestMocker {
        private final WireMockServer wireMockServer;
        private final ObjectMapper objectMapper;

        public TestMocker(WireMockServer wireMockServer, ObjectMapper objectMapper) {
            this.wireMockServer = wireMockServer;
            this.objectMapper = objectMapper;
        }

        public GetRequestStubber<Integer> stubGetDoubleGivenInt(int input) {
            String urlPath = UriBuilder.fromResource(TestResource.class)
                    .path(TestResource.class, "getDoubleGivenInt")
                    .build(input)
                    .toString();
            return new GetRequestStubber<>(wireMockServer, objectMapper, get(urlPathEqualTo(urlPath)));
        }

        public GetRequestVerifier verifyGetDoubleGivenInt(int input) {
            String urlPath = UriBuilder.fromResource(TestResource.class)
                    .path(TestResource.class, "getDoubleGivenInt")
                    .build(input)
                    .toString();
            return new GetRequestVerifier(wireMockServer, getRequestedFor(urlPathEqualTo(urlPath)));
        }

        public ListRequestStubber<Integer> stubGetListOfInts() {
            String urlPath = UriBuilder.fromResource(TestResource.class)
                    .path(TestResource.class, "getListOfInts")
                    .build()
                    .toString();
            Collection<Integer> collection = new ArrayList<>();
            return new ListRequestStubber<>(wireMockServer, objectMapper, get(urlPathEqualTo(urlPath)), collection);
        }

        public GetRequestVerifier verifyGetListOfInts() {
            String urlPath = UriBuilder.fromResource(TestResource.class)
                    .path(TestResource.class, "getListOfInts")
                    .build()
                    .toString();
            return new GetRequestVerifier(wireMockServer, getRequestedFor(urlPathEqualTo(urlPath)));
        }

        public ListRequestStubber<Integer> stubGetOdds(int lessThan) {
            String urlPath = UriBuilder.fromResource(TestResource.class)
                    .path(TestResource.class, "getOdds")
                    .build()
                    .toString();
            Collection<Integer> collection = new ArrayList<>();
            MappingBuilder mappingBuilder = get(urlPathEqualTo(urlPath))
                    .withQueryParam("lessThan", equalTo(Integer.toString(lessThan)));
            return new ListRequestStubber<>(wireMockServer, objectMapper, mappingBuilder, collection);
        }

        public GetRequestVerifier verifyGetOdds(int lessThan) {
            String urlPath = UriBuilder.fromResource(TestResource.class)
                    .path(TestResource.class, "getOdds")
                    .build()
                    .toString();
            RequestPatternBuilder patternBuilder = getRequestedFor(urlPathEqualTo(urlPath))
                    .withQueryParam("lessThan", equalTo(Integer.toString(lessThan)));
            return new GetRequestVerifier(wireMockServer, patternBuilder);
        }

        public PostRequestStubber<String, Integer> stubPostName() {
            String urlPath = UriBuilder.fromResource(TestResource.class)
                    .path(TestResource.class, "postName")
                    .build()
                    .toString();
            MappingBuilder mappingBuilder = post(urlPathEqualTo(urlPath));
            return new PostRequestStubber<>(wireMockServer, objectMapper, mappingBuilder);
        }

        public PostRequestVerifier<String> verifyPostName() {
            String urlPath = UriBuilder.fromResource(TestResource.class)
                    .path(TestResource.class, "postName")
                    .build()
                    .toString();
            RequestPatternBuilder patternBuilder = postRequestedFor(urlPathEqualTo(urlPath));
            return new PostRequestVerifier<>(wireMockServer, objectMapper, patternBuilder);
        }
    }

    @Path("/test")
    public static class TestResource {
        @GET
        @Path("double/{input}")
        public int getDoubleGivenInt(@PathParam("input") int input) {
            return 2*input;
        }

        @GET
        @Path("list")
        public Collection<Integer> getListOfInts() {
            return ImmutableList.of(1,2,3);
        }

        @GET
        @Path("odds")
        public Collection<Integer> getOdds(@QueryParam("lessThan") int lessThan) {
            return ImmutableList.of(1,3,5);
        }

        @POST
        @Path("name")
        public int postName(String name) { return 1; }
    }

    public static class TestClient {
        private final Client client = ClientBuilder.newClient().register(new JacksonJaxbJsonProvider());

        public int getDoubleGivenInt(int input) {
            return client
                    .target(UriBuilder
                            .fromResource(TestResource.class)
                            .path(TestResource.class, "getDoubleGivenInt")
                            .scheme("http")
                            .host("localhost")
                            .port(8080))
                    .resolveTemplate("input", input)
                    .request()
                    .get()
                    .readEntity(Integer.class);
        }

        public Collection<Integer> getListOfInts() {
            return client
                    .target(UriBuilder
                            .fromResource(TestResource.class)
                            .path(TestResource.class, "getListOfInts")
                            .scheme("http")
                            .host("localhost")
                            .port(8080))
                    .request()
                    .get()
                    .readEntity(new GenericType<Collection<Integer>>(){});
        }

        public Response getResponseForListOfInts() {
            return client
                    .target(UriBuilder
                            .fromResource(TestResource.class)
                            .path(TestResource.class, "getListOfInts")
                            .scheme("http")
                            .host("localhost")
                            .port(8080))
                    .request()
                    .get();
        }

        public Collection<Integer> getOddsLessThan(int lessThan) {
            return client
                    .target(UriBuilder
                            .fromResource(TestResource.class)
                            .path(TestResource.class, "getOdds")
                            .queryParam("lessThan", lessThan)
                            .scheme("http")
                            .host("localhost")
                            .port(8080))
                    .request()
                    .get()
                    .readEntity(new GenericType<Collection<Integer>>(){});
        }

        public Integer postName(String name) throws JsonProcessingException {
            ObjectMapper objectMapper = new ObjectMapper();
            String nameJson = objectMapper.writeValueAsString(name);
            return client
                    .target(UriBuilder
                            .fromResource(TestResource.class)
                            .path(TestResource.class, "postName")
                            .scheme("http")
                            .host("localhost")
                            .port(8080))
                    .request()
                    .post(Entity.json(nameJson))
                    .readEntity(Integer.class);
        }
    }
}
