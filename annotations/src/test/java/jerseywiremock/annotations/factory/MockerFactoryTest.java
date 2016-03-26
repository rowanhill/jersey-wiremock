package jerseywiremock.annotations.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.collect.ImmutableList;
import io.dropwizard.jersey.params.DateTimeParam;
import jerseywiremock.annotations.*;
import jerseywiremock.annotations.handler.requestmapping.paramdescriptors.ParamMatchingStrategy;
import jerseywiremock.core.stub.ListRequestStubber;
import jerseywiremock.core.verify.GetRequestVerifier;
import jerseywiremock.formatter.ParamFormatter;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.UriBuilder;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

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
        @WireMockStub("getIntsByDate")
        ListRequestStubber<Integer> stubGetIntsByDate(DateTime dateTime);

        @WireMockVerify("getIntsByDate")
        GetRequestVerifier verifyGetIntsByDate(DateTime dateTime);

        @WireMockStub("getIntsByDate")
        ListRequestStubber<Integer> stubGetIntsByDateContaining(
                @ParamMatchedBy(ParamMatchingStrategy.CONTAINING) String dateSubstring
        );
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
        public Collection<Integer> getIntsByDate(
                @QueryParam("date") @ParamFormat(DateTimeFormatter.class) DateTimeParam dateParam
        ) {
            return ImmutableList.of(4,5,6);
        }
    }

    public static class DateTimeFormatter implements ParamFormatter<DateTime> {
        public String format(DateTime param) {
            return param.toString(ISODateTimeFormat.date());
        }
    }

    public static class TestClient {
        private final Client client = ClientBuilder.newClient().register(new JacksonJaxbJsonProvider());

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
    }
}