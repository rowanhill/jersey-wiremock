package io.jerseywiremock.annotations.factory;

import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static io.jerseywiremock.annotations.ParamMatchingStrategy.CONTAINING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collection;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.JacksonSerializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.collect.ImmutableList;

import io.jerseywiremock.annotations.ParamFormat;
import io.jerseywiremock.annotations.ParamMatchedBy;
import io.jerseywiremock.annotations.WireMockForResource;
import io.jerseywiremock.annotations.WireMockStub;
import io.jerseywiremock.annotations.WireMockVerify;
import io.jerseywiremock.annotations.formatter.ParamFormatter;
import io.jerseywiremock.core.stub.request.DeleteRequestStubber;
import io.jerseywiremock.core.stub.request.GetListRequestStubber;
import io.jerseywiremock.core.stub.request.GetSingleRequestStubber;
import io.jerseywiremock.core.stub.request.PostRequestStubber;
import io.jerseywiremock.core.stub.request.PutRequestStubber;
import io.jerseywiremock.core.stub.request.Serializers;
import io.jerseywiremock.core.verify.DeleteRequestVerifier;
import io.jerseywiremock.core.verify.GetRequestVerifier;
import io.jerseywiremock.core.verify.PostRequestVerifier;
import io.jerseywiremock.core.verify.PutRequestVerifier;

public class MockerFactoryTest {

    private final WireMockServer wireMockServer = new WireMockRule(8080);

    private TestClient client;
    private TestMockerInterface mocker;

    @BeforeEach
    public void setUp() throws Exception {
        wireMockServer.start();
        client = new TestClient();
        Serializers serializers = new Serializers();
        serializers.addSerializer("application/json", new JacksonSerializer());
        mocker = MockerFactory.wireMockerFor(TestMockerInterface.class, new WireMock(8080), serializers);
    }

    @AfterEach
    void after() {
        wireMockServer.stop();
    }

    @Test
    public void getWithSimpleResponseCanBeStubbed() throws Exception {
        // given
        mocker.stubGetInt().andRespondWith(1).stub();

        // when
        int returnedInt = client.getInt();

        // then
        assertThat(returnedInt).isEqualTo(1);
    }

    @Test
    public void getWithQueryParamNeedingFormattingCanBeStubbedAndVerified() throws Exception {
        // given
        DateTime now = DateTime.now();
        mocker.stubGetIntsByDate(now).andRespondWith(4, 5, 6).stub();

        // when
        Collection<Integer> intsByDate = client.getIntsByDate(now);

        // then
        assertThat(intsByDate).containsOnly(4, 5, 6);
        mocker.verifyGetIntsByDate(now).verify();
    }

    @Test
    public void requestWithQueryParamMatchedByContainingCanBeStubbedAndVerified() throws Exception {
        // given
        DateTime now = DateTime.now();
        String year = Integer.toString(now.getYear());
        mocker.stubGetIntsByDateContaining(year).andRespondWith(4, 5, 6).stub();

        // when
        Collection<Integer> intsByDate = client.getIntsByDate(now);

        // then
        assertThat(intsByDate).containsOnly(4, 5, 6);
        mocker.verifyGetIntsByDate(now).verify();
    }

    @Test
    public void postRequestWithBodyCanBeStubbedAndVerified() throws Exception {
        // given
        mocker.stubPostString().withRequestEntity(new StringHolder("req")).andRespondWith(new StringWithId(1, "req")).stub();

        // when
        StringWithId response = client.postString(new StringHolder("req"));

        // then
        assertThat(response).isEqualToComparingFieldByField(new StringWithId(1, "req"));
        mocker.verifyPostString().withRequestEntity(new StringHolder("req"));
    }

    @Test
    public void postRequestWithRequestBodyMatchedByContainingCanBeStubbedAndVerified() throws Exception {
        // given
        mocker.stubPostString().withRequestBody(containing("quest")).andRespondWith(new StringWithId(1, "req")).stub();

        // when
        StringWithId response = client.postString(new StringHolder("Request string"));

        // then
        assertThat(response).isEqualToComparingFieldByField(new StringWithId(1, "req"));
        mocker.verifyPostString().withRequestBody(containing("quest")).verify();
    }

    @Test
    public void putRequestWithBodyCanBeStubbedAndVerified() throws Exception {
        // given
        mocker.stubPutString(1).withRequestEntity(new StringHolder("updated")).andRespondWith(new StringWithId(1, "updated")).stub();

        // when
        StringWithId response = client.putString(1, new StringHolder("updated"));

        // then
        assertThat(response).isEqualToComparingFieldByField(new StringWithId(1, "updated"));
        mocker.verifyPutString(1).withRequestEntity(new StringHolder("updated"));
    }

    @Test
    public void deleteRequestWithBodyCanBeStubbedAndVerified() throws Exception {
        // given
        mocker.stubDeleteString(1).andRespond().stub();

        // when
        Response response = client.deleteString(1);

        // then
        assertThat(response.getStatus()).isEqualTo(204);
        mocker.verifyDeleteString(1);
    }

    @Test
    public void callingInterfaceMethodWithWrongReturnTypeThrowsException() {
        // given
        Serializers serializers = new Serializers();

        // when
        assertThrows(Exception.class, () -> MockerFactory.wireMockerFor(TestBadMockerInterface.class, new WireMock(8080), serializers));
    }

    @Test
    public void omittedQueryParametersAllowMatchingAgainstAnything() throws Exception {
        // given
        mocker.stubGetIntsByAnyDate().andRespondWith(4, 5, 6).stub();

        // when
        Collection<Integer> intsByDate = client.getIntsByDate(DateTime.now());

        // then
        assertThat(intsByDate).containsOnly(4, 5, 6);
        mocker.verifyGetIntsByAnyDate().verify();
    }

    @WireMockForResource(TestResource.class)
    public interface TestMockerInterface {
        @WireMockStub("getInt")
        GetSingleRequestStubber<Integer> stubGetInt();

        @WireMockStub("getIntsByDate")
        GetListRequestStubber<Integer> stubGetIntsByDate(DateTime dateTime);

        @WireMockVerify("getIntsByDate")
        GetRequestVerifier verifyGetIntsByDate(DateTime dateTime);

        @WireMockStub("getIntsByDate")
        GetListRequestStubber<Integer> stubGetIntsByAnyDate();

        @WireMockVerify("getIntsByDate")
        GetRequestVerifier verifyGetIntsByAnyDate();

        @WireMockStub("getIntsByDate")
        GetListRequestStubber<Integer> stubGetIntsByDateContaining(@ParamMatchedBy(CONTAINING) String dateSubstring);

        @WireMockStub("postString")
        PostRequestStubber<StringHolder, StringWithId> stubPostString();

        @WireMockVerify("postString")
        PostRequestVerifier<StringHolder> verifyPostString();

        @WireMockStub("putString")
        PutRequestStubber<StringHolder, StringWithId> stubPutString(int id);

        @WireMockVerify("putString")
        PutRequestVerifier<StringHolder> verifyPutString(int id);

        @WireMockStub("deleteString")
        DeleteRequestStubber stubDeleteString(int id);

        @WireMockVerify("deleteString")
        DeleteRequestVerifier verifyDeleteString(int id);
    }

    public static class StringWithId {
        @JsonProperty
        public int id;
        @JsonProperty
        public String string;

        @SuppressWarnings("unused")
        public StringWithId() {
            // Jackson
        }

        public StringWithId(int id, String string) {
            this.id = id;
            this.string = string;
        }
    }

    public static class StringHolder {
        @JsonProperty
        public String string;

        @SuppressWarnings("unused")
        public StringHolder() {
            // Jackson
        }

        public StringHolder(String string) {
            this.string = string;
        }
    }

    @SuppressWarnings("unused")
    @WireMockForResource(TestResource.class)
    public interface TestBadMockerInterface {
        @WireMockStub("getDoubleGivenInt")
        void stubGetDoubleGivenInt(int input);

        @WireMockVerify("getDoubleGivenInt")
        int verifyGetDoubleGivenInt(int input);
    }

    @Path("/test")
    public static class TestResource {
        @GET
        @Path("simple")
        public int getInt() {
            return 1;
        }

        @GET
        public Collection<Integer> getIntsByDate(
                @QueryParam("date") @ParamFormat(DateTimeFormatter.class) DateTime date
        ) {
            return ImmutableList.of(4,5,6);
        }

        @POST
        @Path("string")
        public StringWithId postString(String entity) {
            return new StringWithId(1, entity);
        }

        @PUT
        @Path("string/{id}")
        public StringWithId putString(@PathParam("id") int id, String entity) {
            return new StringWithId(id, entity);
        }

        @DELETE
        @Path("string/{id}")
        public void deleteString(@PathParam("id") int id) {
        }
    }

    public static class DateTimeFormatter implements ParamFormatter<DateTime> {
        public String format(DateTime param) {
            return param.toString(ISODateTimeFormat.date());
        }
    }

    public static class TestClient {
        private final Client client = ClientBuilder.newClient().register(JacksonJsonProvider.class);

        public int getInt() {
            return client
                    .target(UriBuilder
                            .fromResource(TestResource.class)
                            .path(TestResource.class, "getInt")
                            .scheme("http")
                            .host("localhost")
                            .port(8080))
                    .request()
                    .get()
                    .readEntity(Integer.class);
        }

        public Collection<Integer> getIntsByDate(DateTime dateTime) {
            return client
                    .target(UriBuilder
                            .fromResource(TestResource.class)
                            .queryParam("date", dateTime.toString(ISODateTimeFormat.date()))
                            .scheme("http")
                            .host("localhost")
                            .port(8080))
                    .request()
                    .get()
                    .readEntity(new GenericType<Collection<Integer>>(){});
        }

        public StringWithId postString(StringHolder req) {
            return client
                    .target(UriBuilder
                            .fromResource(TestResource.class)
                            .path(TestResource.class, "postString")
                            .scheme("http")
                            .host("localhost")
                            .port(8080))
                    .request()
                    .post(Entity.json(req))
                    .readEntity(StringWithId.class);
        }

        public StringWithId putString(int id, StringHolder req) {
            return client
                    .target(UriBuilder
                            .fromResource(TestResource.class)
                            .path(TestResource.class, "putString")
                            .scheme("http")
                            .host("localhost")
                            .port(8080)
                            .build(id))
                    .request()
                    .put(Entity.json(req))
                    .readEntity(StringWithId.class);
        }

        public Response deleteString(int id) {
            return client
                    .target(UriBuilder
                            .fromResource(TestResource.class)
                            .path(TestResource.class, "deleteString")
                            .scheme("http")
                            .host("localhost")
                            .port(8080)
                            .build(id))
                    .request()
                    .delete();
        }
    }
}