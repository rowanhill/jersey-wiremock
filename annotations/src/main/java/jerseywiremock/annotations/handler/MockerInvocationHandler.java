package jerseywiremock.annotations.handler;

import jerseywiremock.annotations.WireMockStub;
import jerseywiremock.annotations.WireMockVerify;
import jerseywiremock.core.stub.GetRequestMocker;
import jerseywiremock.core.stub.ListRequestMocker;
import jerseywiremock.core.verify.GetRequestVerifier;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.This;

import java.lang.reflect.Method;
import java.util.Collection;

public class MockerInvocationHandler {
    private final MockerMethodDescriptorFactory mockerMethodDescriptorFactory;

    public MockerInvocationHandler(MockerMethodDescriptorFactory mockerMethodDescriptorFactory) {
        this.mockerMethodDescriptorFactory = mockerMethodDescriptorFactory;
    }

    public <T> GetRequestMocker<T> handleStubGet(
            @AllArguments Object[] parameters,
            @This BaseMocker mocker,
            @Origin Method method
    ) {
        // TODO: Check method is @GET annotated
        MockerMethodDescriptor descriptor =
                mockerMethodDescriptorFactory.constructMethodDescriptor(parameters, method, WireMockStub.class);
        return new GetRequestMocker<>(
                mocker.wireMockServer,
                mocker.objectMapper,
                descriptor.getRequestMappingDescriptor());
    }

    public <T> ListRequestMocker<T> handleStubList(
            @AllArguments Object[] parameters,
            @This BaseMocker mocker,
            @Origin Method method
    ) {
        // TODO: Check method is @GET annotated
        MockerMethodDescriptor descriptor =
                mockerMethodDescriptorFactory.constructMethodDescriptor(parameters, method, WireMockStub.class);
        Collection<T> collection =
                CollectionFactory.createCollection(descriptor.getResourceClass(), descriptor.getMethodName());
        return new ListRequestMocker<>(
                mocker.wireMockServer,
                mocker.objectMapper,
                descriptor.getRequestMappingDescriptor(), collection);
    }

    public GetRequestVerifier handleVerifyGetVerb(
            @AllArguments Object[] parameters,
            @This BaseMocker mocker,
            @Origin Method method
    ) {
        // TODO: Check method is @GET annotated
        MockerMethodDescriptor descriptor =
                mockerMethodDescriptorFactory.constructMethodDescriptor(parameters, method, WireMockVerify.class);
        return new GetRequestVerifier(mocker.wireMockServer, descriptor.getRequestMappingDescriptor());
    }

}
