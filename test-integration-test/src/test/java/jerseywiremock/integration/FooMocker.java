package jerseywiremock.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import jerseywiremock.annotations.handler.MockerInvocationHandler;
import jerseywiremock.core.stub.GetRequestMocker;
import jerseywiremock.core.stub.ListRequestMocker;
import jerseywiremock.core.verify.GetRequestVerifier;
import jerseywiremock.service.core.Foo;
import jerseywiremock.service.resources.FooResource;

/*
@WireMockForResource(FooResource.class)
public interface FooMocker {
    @WireMockStub("getById")
    GetRequestMocker<Foo> stubGetFoo(int id);

    @WireMockVerify("getById")
    GetRequestVerifier verifyGetFoo(int id);

    @WireMockStub("getAllByName")
    ListRequestMocker<Foo> stubListFoos(String name);

    @WireMockVerify("getAllByName")
    GetRequestVerifier verifyListFoos(String name);
}
*/

// TODO: Define as an annotated interface (as above), and construct with ByteBuddy
public class FooMocker {
    private final WireMockServer wireMockServer;
    private final ObjectMapper objectMapper;

    public FooMocker(WireMockServer wireMockServer, ObjectMapper objectMapper) {
        this.wireMockServer = wireMockServer;
        this.objectMapper = objectMapper;
    }

    public GetRequestMocker<Foo> stubGetFoo(int id) {
        MockerInvocationHandler handler = new MockerInvocationHandler(FooResource.class, "getById", wireMockServer, objectMapper);
        return handler.handleStubGet(new Object[]{id});
    }

    public GetRequestVerifier verifyGetFoo(int id) {
        MockerInvocationHandler handler = new MockerInvocationHandler(FooResource.class, "getById", wireMockServer, objectMapper);
        return handler.handleVerifyGet(new Object[]{id});
    }

    public ListRequestMocker<Foo> stubListFoos(String name) {
        MockerInvocationHandler handler = new MockerInvocationHandler(FooResource.class, "getAllByName", wireMockServer, objectMapper);
        return handler.handleStubList(new Object[]{name});
    }

    public GetRequestVerifier verifyListFoos(String name) {
        MockerInvocationHandler handler = new MockerInvocationHandler(FooResource.class, "getAllByName", wireMockServer, objectMapper);
        return handler.handleVerifyList(new Object[]{name});
    }
}
