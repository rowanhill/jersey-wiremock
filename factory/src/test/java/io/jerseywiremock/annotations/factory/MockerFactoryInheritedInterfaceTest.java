package io.jerseywiremock.annotations.factory;

import static org.assertj.core.api.Assertions.assertThat;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.UriBuilder;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.JacksonSerializer;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

import io.jerseywiremock.annotations.WireMockForResource;
import io.jerseywiremock.annotations.WireMockStub;
import io.jerseywiremock.core.stub.request.GetSingleRequestStubber;
import io.jerseywiremock.core.stub.request.Serializers;

public class MockerFactoryInheritedInterfaceTest {
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8080);

    private TestClient client;
    private TestMocker mocker;

    @Before
    public void setUp() throws Exception {
        client = new TestClient();
        Serializers serializers = new Serializers();
        serializers.addSerializer("application/json", new JacksonSerializer());
        mocker = MockerFactory.wireMockerFor(TestMocker.class, new WireMock(8080), serializers);
    }

    @Test
    public void methodFromParentInterfacesCanBeCalled() throws Exception {
        // given
        mocker.stubGetFoo().andRespondWith(10).stub();
        mocker.stubGetBar().andRespondWith(20).stub();

        // when
        int foo = client.getFoo();
        int bar = client.getBar();

        // then
        assertThat(foo).isEqualTo(10);
        assertThat(bar).isEqualTo(20);
    }

    public interface TestMocker extends TestFooMocker, TestBarMocker {}

    @WireMockForResource(TestFooResource.class)
    public interface TestFooMocker {
        @WireMockStub("getFoo")
        GetSingleRequestStubber<Integer> stubGetFoo();
    }

    @Path("/foo")
    public static class TestFooResource {
        @GET
        public int getFoo() {
            return 1;
        }
    }

    @WireMockForResource(TestBarResource.class)
    public interface TestBarMocker {
        @WireMockStub("getBar")
        GetSingleRequestStubber<Integer> stubGetBar();
    }

    @Path("/bar")
    public static class TestBarResource {
        @GET
        public int getBar() {
            return 1;
        }
    }

    public static class TestClient {
        private final Client client = ClientBuilder.newClient().register(JacksonJsonProvider.class);

        public int getFoo() {
            return client
                    .target(UriBuilder
                            .fromResource(TestFooResource.class)
                            .scheme("http")
                            .host("localhost")
                            .port(8080))
                    .request()
                    .get()
                    .readEntity(Integer.class);
        }

        public int getBar() {
            return client
                    .target(UriBuilder
                            .fromResource(TestBarResource.class)
                            .scheme("http")
                            .host("localhost")
                            .port(8080))
                    .request()
                    .get()
                    .readEntity(Integer.class);
        }
    }
}
