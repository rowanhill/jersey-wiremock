package jerseywiremock.annotations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import jerseywiremock.core.stub.GetRequestMocker;
import org.junit.Rule;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

public class MockerFactoryTest {
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8080);

    @Test
    public void mockerCanBeCreated() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        TestMockerInterface mocker = MockerFactory.wireMockerFor(TestMockerInterface.class, wireMockRule, objectMapper);

        mocker.stubGetDoubleGivenInt(1).andRespondWith(5).stub();

        // TODO: Make HTTP call, check result is as expected
        System.out.println(wireMockRule.listAllStubMappings().getMappings());
    }

    @WireMockForResource(TestResource.class)
    public interface TestMockerInterface {
        @WireMockStub("getDoubleGivenInt")
        GetRequestMocker<Integer> stubGetDoubleGivenInt(int input);
    }

    @Path("/test")
    public static class TestResource {
        @GET
        @Path("double/{input}")
        public Integer getDoubleGivenInt(@PathParam("input") int input) {
            return 2*input;
        }
    }
}