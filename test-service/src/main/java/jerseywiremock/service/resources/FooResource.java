package jerseywiremock.service.resources;

import io.dropwizard.jersey.params.IntParam;
import jerseywiremock.service.core.Foo;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Path("/foo")
@Produces(MediaType.APPLICATION_JSON)
public class FooResource {
    @GET
    @Path("{id}")
    public Foo getById(@PathParam("id") IntParam idParam) {
        return new Foo(idParam.get(), "Test foo");
    }

    // TODO: Add a more troublesome data type as a param - e.g. DateTimeParam
    @GET
    public Collection<Foo> getAllByName(@QueryParam("name") String name) {
        List<Foo> foos = new ArrayList<Foo>();
        foos.add(new Foo(1, name));
        foos.add(new Foo(2, name));
        return foos;
    }

    // TODO: @POST, @PUT and @DELETE endpoints
}
