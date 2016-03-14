package example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import jerseywiremock.annotations.JerseyWireMock;
import org.junit.Rule;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ExampleIntegrationTest {
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8080);

    @JerseyWireMock(FooResource.class)
    public FooMocker fooMocker = new FooMocker(wireMockRule, new ObjectMapper());


    @Path("/foo")
    @Produces(MediaType.APPLICATION_JSON)
    public static class FooResource {
        @GET
        @Path("{id}")
        public Foo getById(@PathParam("clubId") int id) {
            return new Foo(id, "Test foo");
        }

        @GET
        public Collection<Foo> getAllByName(@QueryParam("name") String name) {
            List<Foo> foos = new ArrayList<Foo>();
            foos.add(new Foo(1, name));
            foos.add(new Foo(2, name));
            return foos;
        }
    }

    public static class Foo {
        private final int id;
        private final String name;

        public Foo(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}
