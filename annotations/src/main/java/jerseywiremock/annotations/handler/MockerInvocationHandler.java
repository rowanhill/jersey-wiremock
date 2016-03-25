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
    private final ResourceMethodDescriptorFactory resourceMethodDescriptorFactory;
    private final CollectionFactory collectionFactory;

    public MockerInvocationHandler(
            ResourceMethodDescriptorFactory resourceMethodDescriptorFactory,
            CollectionFactory collectionFactory
    ) {
        this.resourceMethodDescriptorFactory = resourceMethodDescriptorFactory;
        this.collectionFactory = collectionFactory;
    }

    public <T> GetRequestMocker<T> handleStubGet(
            @AllArguments Object[] parameters,
            @This BaseMocker mocker,
            @Origin Method method
    ) {
        ResourceMethodDescriptor descriptor =
                resourceMethodDescriptorFactory.constructMethodDescriptor(parameters, method, WireMockStub.class);
        assertVerb(descriptor, HttpVerb.GET);
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
        ResourceMethodDescriptor descriptor =
                resourceMethodDescriptorFactory.constructMethodDescriptor(parameters, method, WireMockStub.class);
        assertVerb(descriptor, HttpVerb.GET);
        Collection<T> collection =
                collectionFactory.createCollection(descriptor.getResourceClass(), descriptor.getMethodName());
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
        ResourceMethodDescriptor descriptor =
                resourceMethodDescriptorFactory.constructMethodDescriptor(parameters, method, WireMockVerify.class);
        assertVerb(descriptor, HttpVerb.GET);
        return new GetRequestVerifier(mocker.wireMockServer, descriptor.getRequestMappingDescriptor());
    }

    private void assertVerb(ResourceMethodDescriptor descriptor, HttpVerb verb) {
        if (descriptor.getVerb() != HttpVerb.GET) {
            throw new RuntimeException("Expected " + descriptor.getMethodName() + " to be annotated with @"
                    + verb.getAnnotation().getSimpleName());
        }
    }

}
