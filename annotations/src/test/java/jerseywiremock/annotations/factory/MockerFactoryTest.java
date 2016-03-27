package jerseywiremock.annotations.factory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.collect.ImmutableList;
import io.dropwizard.jersey.params.DateTimeParam;
import jerseywiremock.annotations.*;
import jerseywiremock.core.stub.request.*;
import jerseywiremock.core.verify.DeleteRequestVerifier;
import jerseywiremock.core.verify.GetRequestVerifier;
import jerseywiremock.core.verify.PostRequestVerifier;
import jerseywiremock.core.verify.PutRequestVerifier;
import jerseywiremock.formatter.ParamFormatter;
import org.glassfish.jersey.message.internal.MessageBodyProviderNotFoundException;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
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
import java.util.Collection;

import static jerseywiremock.annotations.handler.requestmatching.paramdescriptors.ParamMatchingStrategy.CONTAINING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class MockerFactoryTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8080);

    private TestClient client;
    private TestMockerInterface mocker;

    @Before
    public void setUp() throws Exception {
        client = new TestClient();
        ObjectMapper objectMapper = new ObjectMapper();
        mocker = MockerFactory.wireMockerFor(TestMockerInterface.class, wireMockRule, objectMapper);
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
        mocker.stubPostString("req").andRespondWith(new StringWithId(1, "req")).stub();

        // when
        StringWithId response = client.postString("req");

        // then
        assertThat(response).isEqualToComparingFieldByField(new StringWithId(1, "req"));
        mocker.verifyPostString("req");
    }

    @Test
    public void postRequestWithBodyIsNotMatchedIfWrongRequestBodyIsGiven() throws Exception {
        // given
        mocker.stubPostString("expected").andRespondWith(null).stub();

        // when
        try {
            client.postString("wrong");
            fail("Expected MessageBodyProviderNotFoundException when POSTing a string not matched");
        } catch (MessageBodyProviderNotFoundException ignored) {
            // This exception is required - WireMock will return HTML in the 404, which the client can't parse
        }

        // then
        mocker.verifyPostString("expected").times(0).verify();
    }

    @Test
    public void postRequestWithRequestBodyMatchedByContainingCanBeStubbedAndVerified() throws Exception {
        // given
        mocker.stubPostStringContaining("quest").andRespondWith(new StringWithId(1, "req")).stub();

        // when
        StringWithId response = client.postString("Request string");

        // then
        assertThat(response).isEqualToComparingFieldByField(new StringWithId(1, "req"));
            mocker.verifyPostStringContaining("quest");
    }

    @Test
    public void putRequestWithBodyCanBeStubbedAndVerified() throws Exception {
        // given
        mocker.stubPutString(1, "updated").andRespondWith(new StringWithId(1, "updated")).stub();

        // when
        StringWithId response = client.putString(1, "updated");

        // then
        assertThat(response).isEqualToComparingFieldByField(new StringWithId(1, "updated"));
        mocker.verifyPutString(1, "updated");
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
    public void callingInterfaceMethodWithWrongReturnTypeThrowsException() throws Exception {
        // given
        ObjectMapper objectMapper = new ObjectMapper();

        // when
        expectedException.expectMessage("All methods must return request stubbers or verifiers");
        expectedException.expectMessage("stubGetDoubleGivenInt");
        expectedException.expectMessage("verifyGetDoubleGivenInt");
        MockerFactory.wireMockerFor(TestBadMockerInterface.class, wireMockRule, objectMapper);
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
        GetListRequestStubber<Integer> stubGetIntsByDateContaining(@ParamMatchedBy(CONTAINING) String dateSubstring);

        @WireMockStub("postString")
        PostRequestStubber<String, StringWithId> stubPostString(String req);

        @WireMockVerify("postString")
        PostRequestVerifier<String> verifyPostString(String req);

        @WireMockStub("postString")
        PostRequestStubber<String, StringWithId> stubPostStringContaining(@ParamMatchedBy(CONTAINING) String req);

        @WireMockVerify("postString")
        PostRequestVerifier<String> verifyPostStringContaining(@ParamMatchedBy(CONTAINING) String req);

        @WireMockStub("putString")
        PutRequestStubber<String, StringWithId> stubPutString(int id, String req);

        @WireMockVerify("putString")
        PutRequestVerifier<String> verifyPutString(int id, String req);

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
                @QueryParam("date") @ParamFormat(DateTimeFormatter.class) DateTimeParam dateParam
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
        private final Client client = ClientBuilder.newClient().register(new JacksonJaxbJsonProvider());

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

        public StringWithId postString(String req) {
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

        public StringWithId putString(int id, String req) {
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