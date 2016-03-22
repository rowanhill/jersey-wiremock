package jerseywiremock.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.collect.ImmutableList;
import jerseywiremock.core.stub.GetRequestMocker;
import jerseywiremock.core.stub.ListRequestMocker;
import jerseywiremock.core.verify.GetRequestVerifier;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.UriBuilder;
import java.util.ArrayList;
import java.util.Collection;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

/*
  Note: the tests in this class simply check that mocker calls can be made without exceptions. Testing the actual
  functionality is done in the annotations module.
 */
public class ManualMockerTest {
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8080);
    private ManualTestMocker testMocker;

    @Before
    public void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        testMocker = new ManualTestMocker(wireMockRule, objectMapper);
    }

    @Test
    public void getRequestsCanBeStubbed() throws Exception {
        testMocker.stubGetDoubleGivenInt(2).andRespondWith(4).stub();
    }

    @Test
    public void listRequestsCanBeStubbed() throws Exception {
        testMocker.stubGetListOfInts().andRespondWith(4, 8, 16).stub();
    }

    @Test
    public void getRequestsCanBeVerified() {
        testMocker.verifyGetDoubleGivenInt(2).times(0).verify();
    }

    public static class ManualTestMocker {
        private final WireMockServer wireMockServer;
        private final ObjectMapper objectMapper;

        public ManualTestMocker(WireMockServer wireMockServer, ObjectMapper objectMapper) {
            this.wireMockServer = wireMockServer;
            this.objectMapper = objectMapper;
        }

        public GetRequestMocker<Integer> stubGetDoubleGivenInt(int input) {
            String urlPath = UriBuilder.fromResource(TestResource.class)
                    .path(TestResource.class, "getDoubleGivenInt")
                    .build(input)
                    .toString();
            return new GetRequestMocker<>(wireMockServer, objectMapper, get(urlPathEqualTo(urlPath)));
        }

        public ListRequestMocker<Integer> stubGetListOfInts() {
            String urlPath = UriBuilder.fromResource(TestResource.class)
                    .path(TestResource.class, "getListOfInts")
                    .build()
                    .toString();
            Collection<Integer> collection = new ArrayList<>();
            return new ListRequestMocker<>(wireMockServer, objectMapper, get(urlPathEqualTo(urlPath)), collection);
        }

        public GetRequestVerifier verifyGetDoubleGivenInt(int input) {
            String urlPath = UriBuilder.fromResource(TestResource.class)
                    .path(TestResource.class, "getDoubleGivenInt")
                    .build(input)
                    .toString();
            return new GetRequestVerifier(wireMockServer, getRequestedFor(urlPathEqualTo(urlPath)));
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
    }
}
