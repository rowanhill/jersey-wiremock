package jerseywiremock.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.google.common.collect.ImmutableList;
import jerseywiremock.core.stub.GetRequestMocker;
import jerseywiremock.core.stub.ListRequestMocker;
import jerseywiremock.core.verify.GetRequestVerifier;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.UriBuilder;
import java.util.ArrayList;
import java.util.Collection;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

public class ManualMockerTest {

    // The "tests" here are just ensuring the below compiles; actual functionality is tested in the annotations module

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
