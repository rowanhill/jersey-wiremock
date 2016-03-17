package jerseywiremock.annotations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.collect.ImmutableList;
import io.dropwizard.jersey.params.DateTimeParam;
import jerseywiremock.core.stub.GetRequestMocker;
import jerseywiremock.core.stub.ListRequestMocker;
import jerseywiremock.core.verify.GetRequestVerifier;
import jerseywiremock.formatter.ParamFormatter;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Rule;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.UriBuilder;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

public class MockerFactoryTest {
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8080);

    @Test
    public void mockerCanBeCreated() throws Exception {
        TestClient client = new TestClient();
        ObjectMapper objectMapper = new ObjectMapper();
        TestMockerInterface mocker = MockerFactory.wireMockerFor(TestMockerInterface.class, wireMockRule, objectMapper);
        DateTime now = DateTime.now();

        mocker.stubGetDoubleGivenInt(1).andRespondWith(5).stub();
        mocker.stubGetListOfInts().andRespondWith(1, 2, 3).stub();
        mocker.stubGetIntsByDate(now).andRespondWith(4, 5, 6).stub();

        int doubleOne = client.getDoubleGivenInt(1);
        assertThat(doubleOne).isEqualTo(5);

        Collection<Integer> listOfInts = client.getListOfInts();
        assertThat(listOfInts).containsOnly(1, 2, 3);

        Collection<Integer> intsByDate = client.getIntsByDate(now);
        assertThat(intsByDate).containsOnly(4, 5, 6);

        mocker.verifyGetDoubleGivenInt(1).times(1).verify();
        mocker.verifyGetListOfInts().times(1).verify();
    }

    @WireMockForResource(TestResource.class)
    public interface TestMockerInterface {
        @WireMockStub("getDoubleGivenInt")
        GetRequestMocker<Integer> stubGetDoubleGivenInt(int input);

        @WireMockVerify("getDoubleGivenInt")
        GetRequestVerifier verifyGetDoubleGivenInt(int input);

        @WireMockStub("getListOfInts")
        ListRequestMocker<Integer> stubGetListOfInts();

        @WireMockVerify("getListOfInts")
        GetRequestVerifier verifyGetListOfInts();

        @WireMockStub("getIntsByDate")
        ListRequestMocker<Integer> stubGetIntsByDate(DateTime dateTime);
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
        public Collection<Integer> getIntsByDate(
                @QueryParam("date") @ParamFormat(DateTimeFormatter.class) DateTimeParam dateParam
        ) {
            DateTime dateTime = dateParam.get();
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