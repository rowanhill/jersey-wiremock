package jerseywiremock.annotations.handler;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.RequestPatternBuilder;
import jerseywiremock.annotations.WireMockStub;
import jerseywiremock.annotations.WireMockVerify;
import jerseywiremock.annotations.handler.requestmapping.RequestMappingDescriptor;
import jerseywiremock.annotations.handler.requestmapping.RequestMappingDescriptorFactory;
import jerseywiremock.annotations.handler.resourcemethod.HttpVerb;
import jerseywiremock.annotations.handler.resourcemethod.ResourceMethodDescriptor;
import jerseywiremock.annotations.handler.resourcemethod.ResourceMethodDescriptorFactory;
import jerseywiremock.annotations.handler.util.CollectionFactory;
import jerseywiremock.core.stub.GetRequestStubber;
import jerseywiremock.core.stub.ListRequestStubber;
import jerseywiremock.annotations.handler.requestmapping.stubverbs.GetMappingBuilderStrategy;
import jerseywiremock.core.verify.GetRequestVerifier;
import jerseywiremock.annotations.handler.requestmapping.verifyverbs.GetRequestedForStrategy;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.This;

import java.lang.reflect.Method;
import java.util.Collection;

public class MockerInvocationHandler {
    private final ResourceMethodDescriptorFactory resourceMethodDescriptorFactory;
    private final RequestMappingDescriptorFactory requestMappingDescriptorFactory;
    private final CollectionFactory collectionFactory;

    public MockerInvocationHandler(
            ResourceMethodDescriptorFactory resourceMethodDescriptorFactory,
            RequestMappingDescriptorFactory requestMappingDescriptorFactory,
            CollectionFactory collectionFactory
    ) {
        this.resourceMethodDescriptorFactory = resourceMethodDescriptorFactory;
        this.requestMappingDescriptorFactory = requestMappingDescriptorFactory;
        this.collectionFactory = collectionFactory;
    }

    public <T> GetRequestStubber<T> handleStubGet(
            @AllArguments Object[] parameters,
            @This BaseMocker mocker,
            @Origin Method method
    ) {
        ResourceMethodDescriptor descriptor =
                resourceMethodDescriptorFactory.constructMethodDescriptor(method, WireMockStub.class);
        descriptor.assertVerb(HttpVerb.GET);
        RequestMappingDescriptor mappingDescriptor = requestMappingDescriptorFactory
                .createMappingDescriptor(descriptor, method, parameters);
        MappingBuilder mappingBuilder = mappingDescriptor.toMappingBuilder(new GetMappingBuilderStrategy());
        return new GetRequestStubber<>(
                mocker.wireMockServer,
                mocker.objectMapper,
                mappingBuilder);
    }

    public <T> ListRequestStubber<T> handleStubList(
            @AllArguments Object[] parameters,
            @This BaseMocker mocker,
            @Origin Method method
    ) {
        ResourceMethodDescriptor descriptor =
                resourceMethodDescriptorFactory.constructMethodDescriptor(method, WireMockStub.class);
        descriptor.assertVerb(HttpVerb.GET);
        RequestMappingDescriptor mappingDescriptor = requestMappingDescriptorFactory
                .createMappingDescriptor(descriptor, method, parameters);
        MappingBuilder mappingBuilder = mappingDescriptor.toMappingBuilder(new GetMappingBuilderStrategy());
        Collection<T> collection =
                collectionFactory.createCollection(descriptor.getResourceClass(), descriptor.getMethodName());
        return new ListRequestStubber<>(
                mocker.wireMockServer,
                mocker.objectMapper,
                mappingBuilder,
                collection);
    }

    public GetRequestVerifier handleVerifyGetVerb(
            @AllArguments Object[] parameters,
            @This BaseMocker mocker,
            @Origin Method method
    ) {
        ResourceMethodDescriptor descriptor =
                resourceMethodDescriptorFactory.constructMethodDescriptor(method, WireMockVerify.class);
        descriptor.assertVerb(HttpVerb.GET);
        RequestMappingDescriptor mappingDescriptor = requestMappingDescriptorFactory
                .createMappingDescriptor(descriptor, method, parameters);
        RequestPatternBuilder requestPatternBuilder = mappingDescriptor
                .toRequestPatternBuilder(new GetRequestedForStrategy());
        return new GetRequestVerifier(mocker.wireMockServer, requestPatternBuilder);
    }
}
