package jerseywiremock.annotations.handler;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.RequestPatternBuilder;
import jerseywiremock.annotations.WireMockStub;
import jerseywiremock.annotations.WireMockVerify;
import jerseywiremock.annotations.handler.requestmapping.RequestMappingDescriptor;
import jerseywiremock.annotations.handler.requestmapping.RequestMappingDescriptorFactory;
import jerseywiremock.annotations.handler.requestmapping.stubverbs.GetMappingBuilderStrategy;
import jerseywiremock.annotations.handler.requestmapping.stubverbs.PostMappingBuilderStrategy;
import jerseywiremock.annotations.handler.requestmapping.verifyverbs.GetRequestedForStrategy;
import jerseywiremock.annotations.handler.requestmapping.verifyverbs.PostRequestedForStrategy;
import jerseywiremock.annotations.handler.resourcemethod.HttpVerb;
import jerseywiremock.annotations.handler.resourcemethod.ResourceMethodDescriptor;
import jerseywiremock.annotations.handler.resourcemethod.ResourceMethodDescriptorFactory;
import jerseywiremock.annotations.handler.util.CollectionFactory;
import jerseywiremock.core.stub.GetListRequestStubber;
import jerseywiremock.core.stub.GetSingleRequestStubber;
import jerseywiremock.core.stub.PostRequestStubber;
import jerseywiremock.core.verify.GetRequestVerifier;
import jerseywiremock.core.verify.PostRequestVerifier;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.This;

import java.lang.annotation.Annotation;
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

    public <T> GetSingleRequestStubber<T> handleStubGet(
            @AllArguments Object[] parameters,
            @This BaseMocker mocker,
            @Origin Method method
    ) {
        DescriptorsHolder descriptors = descriptorsForStubGet(parameters, method);
        MappingBuilder mappingBuilder = descriptors.requestMappingDescriptor
                .toMappingBuilder(new GetMappingBuilderStrategy());
        return new GetSingleRequestStubber<>(
                mocker.wireMockServer,
                mocker.objectMapper,
                mappingBuilder);
    }

    public <T> GetListRequestStubber<T> handleStubList(
            @AllArguments Object[] parameters,
            @This BaseMocker mocker,
            @Origin Method method
    ) {
        DescriptorsHolder descriptors = descriptorsForStubGet(parameters, method);
        MappingBuilder mappingBuilder = descriptors.requestMappingDescriptor
                .toMappingBuilder(new GetMappingBuilderStrategy());
        Collection<T> collection = collectionFactory.createCollection(
                descriptors.resourceMethodDescriptor.getResourceClass(),
                descriptors.resourceMethodDescriptor.getMethodName());
        return new GetListRequestStubber<>(
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
        DescriptorsHolder descriptors = descriptorsForVerifyGet(parameters, method);
        RequestPatternBuilder requestPatternBuilder = descriptors.requestMappingDescriptor
                .toRequestPatternBuilder(new GetRequestedForStrategy());
        return new GetRequestVerifier(mocker.wireMockServer, requestPatternBuilder);
    }

    public <Req, Resp> PostRequestStubber<Req, Resp> handleStubPost(
            @AllArguments Object[] parameters,
            @This BaseMocker mocker,
            @Origin Method method
    ) {
        DescriptorsHolder descriptors = descriptorsForStubPost(parameters, method);
        MappingBuilder mappingBuilder = descriptors.requestMappingDescriptor
                .toMappingBuilder(new PostMappingBuilderStrategy());
        return new PostRequestStubber<>(
                mocker.wireMockServer,
                mocker.objectMapper,
                mappingBuilder);
    }

    public <Entity> PostRequestVerifier<Entity> handleVerifyPostVerb(
            @AllArguments Object[] parameters,
            @This BaseMocker mocker,
            @Origin Method method
    ) {
        DescriptorsHolder descriptors = descriptorsForVerifyPost(parameters, method);
        RequestPatternBuilder requestPatternBuilder = descriptors.requestMappingDescriptor
                .toRequestPatternBuilder(new PostRequestedForStrategy());
        return new PostRequestVerifier<>(mocker.wireMockServer, mocker.objectMapper, requestPatternBuilder);
    }

    private DescriptorsHolder descriptorsForStubGet(Object[] parameters, Method method) {
        return createDescriptors(parameters, method, WireMockStub.class, HttpVerb.GET);
    }

    private DescriptorsHolder descriptorsForStubPost(Object[] parameters, Method method) {
        return createDescriptors(parameters, method, WireMockStub.class, HttpVerb.POST);
    }

    private DescriptorsHolder descriptorsForVerifyGet(Object[] parameters, Method method) {
        return createDescriptors(parameters, method, WireMockVerify.class, HttpVerb.GET);
    }

    private DescriptorsHolder descriptorsForVerifyPost(Object[] parameters, Method method) {
        return createDescriptors(parameters, method, WireMockVerify.class, HttpVerb.POST);
    }

    private DescriptorsHolder createDescriptors(
            Object[] parameters,
            Method method,
            Class<? extends Annotation> wireMockAnnotation,
            HttpVerb verb
    ) {
        ResourceMethodDescriptor methodDescriptor =
                resourceMethodDescriptorFactory.constructMethodDescriptor(method, wireMockAnnotation);

        methodDescriptor.assertVerb(verb);

        RequestMappingDescriptor mappingDescriptor = requestMappingDescriptorFactory
                .createMappingDescriptor(methodDescriptor, method, parameters);

        return new DescriptorsHolder(methodDescriptor, mappingDescriptor);
    }

    private static class DescriptorsHolder {
        private final ResourceMethodDescriptor resourceMethodDescriptor;
        private final RequestMappingDescriptor requestMappingDescriptor;

        public DescriptorsHolder(
                ResourceMethodDescriptor resourceMethodDescriptor,
                RequestMappingDescriptor requestMappingDescriptor
        ) {
            this.resourceMethodDescriptor = resourceMethodDescriptor;
            this.requestMappingDescriptor = requestMappingDescriptor;
        }
    }
}
