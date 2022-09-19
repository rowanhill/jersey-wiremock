package io;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.deleteRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Collection;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.google.common.collect.ImmutableList;

import io.jerseywiremock.core.stub.request.DeleteRequestStubber;
import io.jerseywiremock.core.stub.request.GetListRequestStubber;
import io.jerseywiremock.core.stub.request.GetSingleRequestStubber;
import io.jerseywiremock.core.stub.request.PostRequestStubber;
import io.jerseywiremock.core.stub.request.PutRequestStubber;
import io.jerseywiremock.core.stub.request.Serializer;
import io.jerseywiremock.core.verify.DeleteRequestVerifier;
import io.jerseywiremock.core.verify.GetRequestVerifier;
import io.jerseywiremock.core.verify.PostRequestVerifier;
import io.jerseywiremock.core.verify.PutRequestVerifier;

public class MockerIntegrationTest {
    private final WireMockServer wireMockServer = new WireMockServer(8080);
    private TestClient client;
    private TestMocker mocker;

    @BeforeEach
    public void setUp() {
        wireMockServer.start();
        client = new TestClient();
        mocker = new TestMocker(new WireMock(8080), new com.JacksonSerializer());
    }

    @AfterEach
    void after() {
        wireMockServer.stop();
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
    public void sendHeaderCanBeStubbedAndVerified() throws Exception {
        // given
        mocker.stubSendHeader("en").andRespondWith(123).stub();

        // when
        int doubleOne = client.sendHeader("en");

        // then
        assertThat(doubleOne).isEqualTo(123);
//        mocker.verifyGetDoubleGivenInt(1).times(1).verify();
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

    @Test
    public void putRequestCanBeStubbedAndVerified() throws Exception {
        // given
        mocker.stubPutName(10).withRequestEntity("Updated name").andRespondWith(10).stub();

        // when
        int number = client.putName(10, "Updated name");

        // then
        mocker.verifyPutName(10).withRequestEntity("Updated name").verify();
        assertThat(number).isEqualTo(10);
    }

    @Test
    public void deleteRequestCanBeStubbedAndVerified() throws Exception {
        // given
        mocker.stubDeleteName(1).andRespond().withStatusCode(204).stub();

        // when
        Response response = client.deleteName(1);

        // then
        mocker.verifyDeleteName(1).verify();
        assertThat(response.readEntity(String.class)).isEmpty();
    }

    @Test
    public void deleteRequestCanOptionallyHaveResponseBody() throws Exception {
        // given
        mocker.stubDeleteNameAndReturnId(1).andRespondWith(1).stub();

        // when
        Response response = client.deleteName(1);

        // then
        mocker.verifyDeleteName(1).verify();
        assertThat(response.readEntity(Integer.class)).isEqualTo(1);
    }

    @Test
    public void defaultResponseCanBeOverriddenWhenCreatingStubber() throws Exception {
        // given
        Client client = ClientBuilder.newClient().register(new JacksonJaxbJsonProvider());
        DeleteRequestStubber stubber = new DeleteRequestStubber(
                new WireMock(8080),
                new com.JacksonSerializer(),
                delete(urlPathEqualTo("/test")),
                aResponse().withStatus(403));

        // when
        stubber.andRespond().stub();
        Response response = client.target("http://localhost:8080/test").request().delete();

        // then
        assertThat(response.getStatus()).isEqualTo(403);
    }

    @Test
    public void responseFaultsCanBeStubbed() throws Exception {
        // given
        mocker.stubGetDoubleGivenInt(1).andRespond().withFault(Fault.EMPTY_RESPONSE).stub();

        // when
        assertThrows(ProcessingException.class, () -> client.getDoubleGivenInt(1));
    }

    public static class TestMocker {
        private final WireMock wireMock;
        private final Serializer serializer;

        public TestMocker(WireMock wireMock, Serializer serializer) {
            this.wireMock = wireMock;
            this.serializer = serializer;
        }

        public GetSingleRequestStubber<Integer> stubGetDoubleGivenInt(int input) {
            String urlPath = UriBuilder.fromResource(TestResource.class)
                    .path(TestResource.class, "getDoubleGivenInt")
                    .build(input)
                    .toString();
            return new GetSingleRequestStubber<>(wireMock, serializer, get(urlPathEqualTo(urlPath)));
        }

        public GetRequestVerifier verifyGetDoubleGivenInt(int input) {
            String urlPath = UriBuilder.fromResource(TestResource.class)
                    .path(TestResource.class, "getDoubleGivenInt")
                    .build(input)
                    .toString();
            return new GetRequestVerifier(wireMock, getRequestedFor(urlPathEqualTo(urlPath)));
        }

        public GetListRequestStubber<Integer> stubGetListOfInts() {
            String urlPath = UriBuilder.fromResource(TestResource.class)
                    .path(TestResource.class, "getListOfInts")
                    .build()
                    .toString();
            Collection<Integer> collection = new ArrayList<>();
            return new GetListRequestStubber<>(wireMock, serializer, get(urlPathEqualTo(urlPath)), collection);
        }

        public GetRequestVerifier verifyGetListOfInts() {
            String urlPath = UriBuilder.fromResource(TestResource.class)
                    .path(TestResource.class, "getListOfInts")
                    .build()
                    .toString();
            return new GetRequestVerifier(wireMock, getRequestedFor(urlPathEqualTo(urlPath)));
        }

        public GetListRequestStubber<Integer> stubGetOdds(int lessThan) {
            String urlPath = UriBuilder.fromResource(TestResource.class)
                    .path(TestResource.class, "getOdds")
                    .build()
                    .toString();
            Collection<Integer> collection = new ArrayList<>();
            MappingBuilder mappingBuilder = get(urlPathEqualTo(urlPath))
                    .withQueryParam("lessThan", equalTo(Integer.toString(lessThan)));
            return new GetListRequestStubber<>(wireMock, serializer, mappingBuilder, collection);
        }

        public GetRequestVerifier verifyGetOdds(int lessThan) {
            String urlPath = UriBuilder.fromResource(TestResource.class)
                    .path(TestResource.class, "getOdds")
                    .build()
                    .toString();
            RequestPatternBuilder patternBuilder = getRequestedFor(urlPathEqualTo(urlPath))
                    .withQueryParam("lessThan", equalTo(Integer.toString(lessThan)));
            return new GetRequestVerifier(wireMock, patternBuilder);
        }

        public GetSingleRequestStubber<Integer> stubSendHeader(String acceptLanguage) {
            String urlPath = UriBuilder.fromResource(TestResource.class)
                    .path(TestResource.class, "sendHeader")
                    .build()
                    .toString();
            MappingBuilder mappingBuilder = get(urlPathEqualTo(urlPath)).withHeader("Accept-Language", equalTo(acceptLanguage));
            return new GetSingleRequestStubber<>(wireMock, serializer, mappingBuilder);
        }

        public PostRequestStubber<String, Integer> stubPostName() {
            String urlPath = UriBuilder.fromResource(TestResource.class)
                    .path(TestResource.class, "postName")
                    .build()
                    .toString();
            MappingBuilder mappingBuilder = post(urlPathEqualTo(urlPath));
            return new PostRequestStubber<>(wireMock, serializer, mappingBuilder);
        }

        public PostRequestVerifier<String> verifyPostName() {
            String urlPath = UriBuilder.fromResource(TestResource.class)
                    .path(TestResource.class, "postName")
                    .build()
                    .toString();
            RequestPatternBuilder patternBuilder = postRequestedFor(urlPathEqualTo(urlPath));
            return new PostRequestVerifier<>(wireMock, serializer, patternBuilder);
        }

        public PutRequestStubber<String, Integer> stubPutName(int id) {
            String urlPath = UriBuilder.fromResource(TestResource.class)
                    .path(TestResource.class, "putName")
                    .build(id)
                    .toString();
            MappingBuilder mappingBuilder = put(urlPathEqualTo(urlPath));
            return new PutRequestStubber<>(wireMock, serializer, mappingBuilder);
        }

        public PutRequestVerifier<String> verifyPutName(int id) {
            String urlPath = UriBuilder.fromResource(TestResource.class)
                    .path(TestResource.class, "putName")
                    .build(id)
                    .toString();
            RequestPatternBuilder patternBuilder = putRequestedFor(urlPathEqualTo(urlPath));
            return new PutRequestVerifier<>(wireMock, serializer, patternBuilder);
        }

        public DeleteRequestStubber<Void> stubDeleteName(int id) {
            String urlPath = UriBuilder.fromResource(TestResource.class)
                    .path(TestResource.class, "deleteName")
                    .build(id)
                    .toString();
            MappingBuilder mappingBuilder = delete(urlPathEqualTo(urlPath));
            return new DeleteRequestStubber<>(wireMock, serializer, mappingBuilder);
        }

        public DeleteRequestStubber<Integer> stubDeleteNameAndReturnId(int id) {
            String urlPath = UriBuilder.fromResource(TestResource.class)
                    .path(TestResource.class, "deleteName")
                    .build(id)
                    .toString();
            MappingBuilder mappingBuilder = delete(urlPathEqualTo(urlPath));
            return new DeleteRequestStubber<>(wireMock, serializer, mappingBuilder);
        }

        public DeleteRequestVerifier verifyDeleteName(int id) {
            String urlPath = UriBuilder.fromResource(TestResource.class)
                    .path(TestResource.class, "deleteName")
                    .build(id)
                    .toString();
            RequestPatternBuilder patternBuilder = deleteRequestedFor(urlPathEqualTo(urlPath));
            return new DeleteRequestVerifier(wireMock, patternBuilder);
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
        @Path("send-header")
        public int sendHeader(@HeaderParam("Accept-Language") String acceptLanguage) {
            return 0;
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

        @PUT
        @Path("name/{id}")
        public int putName(@PathParam("id") int id, String name) { return 1; }

        @DELETE
        @Path("name/{id}")
        public void deleteName(@PathParam("id") int id) { }
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

        public Integer postName(String name) {
            String nameJson = new com.JacksonSerializer().serialize(name);
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

        public Integer putName(int id, String name) {
            String nameJson = new com.JacksonSerializer().serialize(name);
            return client
                    .target(UriBuilder
                            .fromResource(TestResource.class)
                            .path(TestResource.class, "putName")
                            .scheme("http")
                            .host("localhost")
                            .port(8080)
                            .build(id))
                    .request()
                    .put(Entity.json(nameJson))
                    .readEntity(Integer.class);
        }

        public Response deleteName(int id) {
            return client
                    .target(UriBuilder
                            .fromResource(TestResource.class)
                            .path(TestResource.class, "deleteName")
                            .scheme("http")
                            .host("localhost")
                            .port(8080)
                            .build(id))
                    .request()
                    .delete();
        }

        public int sendHeader(String language) {
            return client
                    .target(UriBuilder
                            .fromResource(TestResource.class)
                            .path(TestResource.class, "sendHeader")
                            .scheme("http")
                            .host("localhost")
                            .port(8080))
                    .request()
                    .header("Accept-Language", language)
                    .get()
                    .readEntity(Integer.class);
        }
    }
}
