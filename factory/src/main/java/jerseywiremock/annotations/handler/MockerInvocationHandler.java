package jerseywiremock.annotations.handler;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import jerseywiremock.annotations.WireMockStub;
import jerseywiremock.annotations.WireMockVerify;
import jerseywiremock.annotations.handler.requestmatching.RequestMatchingDescriptor;
import jerseywiremock.annotations.handler.requestmatching.RequestMatchingDescriptorFactory;
import jerseywiremock.annotations.handler.requestmatching.stubverbs.DeleteMappingBuilderStrategy;
import jerseywiremock.annotations.handler.requestmatching.stubverbs.GetMappingBuilderStrategy;
import jerseywiremock.annotations.handler.requestmatching.stubverbs.PostMappingBuilderStrategy;
import jerseywiremock.annotations.handler.requestmatching.stubverbs.PutMappingBuilderStrategy;
import jerseywiremock.annotations.handler.requestmatching.verifyverbs.DeleteRequestedForStrategy;
import jerseywiremock.annotations.handler.requestmatching.verifyverbs.GetRequestedForStrategy;
import jerseywiremock.annotations.handler.requestmatching.verifyverbs.PostRequestedForStrategy;
import jerseywiremock.annotations.handler.requestmatching.verifyverbs.PutRequestedForStrategy;
import jerseywiremock.annotations.handler.resourcemethod.HttpVerb;
import jerseywiremock.annotations.handler.resourcemethod.ResourceMethodDescriptor;
import jerseywiremock.annotations.handler.resourcemethod.ResourceMethodDescriptorFactory;
import jerseywiremock.annotations.handler.util.CollectionFactory;
import jerseywiremock.core.stub.request.*;
import jerseywiremock.core.verify.DeleteRequestVerifier;
import jerseywiremock.core.verify.GetRequestVerifier;
import jerseywiremock.core.verify.PostRequestVerifier;
import jerseywiremock.core.verify.PutRequestVerifier;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.This;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;

import static jerseywiremock.annotations.handler.requestmatching.RequestMatchingDescriptorFactory.QueryParamEncodingStrategy.ENCODED;
import static jerseywiremock.annotations.handler.requestmatching.RequestMatchingDescriptorFactory.QueryParamEncodingStrategy.UNENCODED;

public class MockerInvocationHandler {
    private final ResourceMethodDescriptorFactory resourceMethodDescriptorFactory;
    private final RequestMatchingDescriptorFactory requestMatchingDescriptorFactory;
    private final CollectionFactory collectionFactory;

    public MockerInvocationHandler(
            ResourceMethodDescriptorFactory resourceMethodDescriptorFactory,
            RequestMatchingDescriptorFactory requestMatchingDescriptorFactory,
            CollectionFactory collectionFactory
    ) {
        this.resourceMethodDescriptorFactory = resourceMethodDescriptorFactory;
        this.requestMatchingDescriptorFactory = requestMatchingDescriptorFactory;
        this.collectionFactory = collectionFactory;
    }

    public <T> GetSingleRequestStubber<T> handleStubGet(
            @AllArguments Object[] parameters,
            @This BaseMocker mocker,
            @Origin Method method
    ) {
        DescriptorsHolder descriptors = descriptorsForStubGet(parameters, method);
        MappingBuilder mappingBuilder = descriptors.requestMatchingDescriptor
                .toMappingBuilder(new GetMappingBuilderStrategy());
        ResponseDefinitionBuilder responseDefinitionBuilder = descriptors.resourceMethodDescriptor
                .toResponseDefinitionBuilder();
        return new GetSingleRequestStubber<>(
                mocker.wireMockServer,
                mocker.objectMapper,
                mappingBuilder,
                responseDefinitionBuilder);
    }

    public <T> GetListRequestStubber<T> handleStubList(
            @AllArguments Object[] parameters,
            @This BaseMocker mocker,
            @Origin Method method
    ) {
        DescriptorsHolder descriptors = descriptorsForStubGet(parameters, method);
        MappingBuilder mappingBuilder = descriptors.requestMatchingDescriptor
                .toMappingBuilder(new GetMappingBuilderStrategy());
        ResponseDefinitionBuilder responseDefinitionBuilder = descriptors.resourceMethodDescriptor
                .toResponseDefinitionBuilder();
        Collection<T> collection = collectionFactory.createCollection(
                descriptors.resourceMethodDescriptor.getResourceClass(),
                descriptors.resourceMethodDescriptor.getMethodName());
        return new GetListRequestStubber<>(
                mocker.wireMockServer,
                mocker.objectMapper,
                mappingBuilder,
                responseDefinitionBuilder,
                collection);
    }

    public GetRequestVerifier handleVerifyGetVerb(
            @AllArguments Object[] parameters,
            @This BaseMocker mocker,
            @Origin Method method
    ) {
        DescriptorsHolder descriptors = descriptorsForVerifyGet(parameters, method);
        RequestPatternBuilder requestPatternBuilder = descriptors.requestMatchingDescriptor
                .toRequestPatternBuilder(new GetRequestedForStrategy());
        return new GetRequestVerifier(mocker.wireMockServer, requestPatternBuilder);
    }

    public <Req, Resp> PostRequestStubber<Req, Resp> handleStubPost(
            @AllArguments Object[] parameters,
            @This BaseMocker mocker,
            @Origin Method method
    ) {
        DescriptorsHolder descriptors = descriptorsForStubPost(parameters, method);
        MappingBuilder mappingBuilder = descriptors.requestMatchingDescriptor
                .toMappingBuilder(new PostMappingBuilderStrategy());
        ResponseDefinitionBuilder responseDefinitionBuilder = descriptors.resourceMethodDescriptor
                .toResponseDefinitionBuilder();
        return new PostRequestStubber<>(
                mocker.wireMockServer,
                mocker.objectMapper,
                mappingBuilder,
                responseDefinitionBuilder);
    }

    public <Entity> PostRequestVerifier<Entity> handleVerifyPostVerb(
            @AllArguments Object[] parameters,
            @This BaseMocker mocker,
            @Origin Method method
    ) {
        DescriptorsHolder descriptors = descriptorsForVerifyPost(parameters, method);
        RequestPatternBuilder requestPatternBuilder = descriptors.requestMatchingDescriptor
                .toRequestPatternBuilder(new PostRequestedForStrategy());
        return new PostRequestVerifier<>(mocker.wireMockServer, mocker.objectMapper, requestPatternBuilder);
    }

    public <Req, Resp> PutRequestStubber<Req, Resp> handleStubPut(
            @AllArguments Object[] parameters,
            @This BaseMocker mocker,
            @Origin Method method
    ) {
        DescriptorsHolder descriptors = descriptorsForStubPut(parameters, method);
        MappingBuilder mappingBuilder = descriptors.requestMatchingDescriptor
                .toMappingBuilder(new PutMappingBuilderStrategy());
        ResponseDefinitionBuilder responseDefinitionBuilder = descriptors.resourceMethodDescriptor
                .toResponseDefinitionBuilder();
        return new PutRequestStubber<>(
                mocker.wireMockServer,
                mocker.objectMapper,
                mappingBuilder,
                responseDefinitionBuilder);
    }

    public <Entity> PutRequestVerifier<Entity> handleVerifyPutVerb(
            @AllArguments Object[] parameters,
            @This BaseMocker mocker,
            @Origin Method method
    ) {
        DescriptorsHolder descriptors = descriptorsForVerifyPut(parameters, method);
        RequestPatternBuilder requestPatternBuilder = descriptors.requestMatchingDescriptor
                .toRequestPatternBuilder(new PutRequestedForStrategy());
        return new PutRequestVerifier<>(mocker.wireMockServer, mocker.objectMapper, requestPatternBuilder);
    }

    public <Entity> DeleteRequestStubber<Entity> handleStubDelete(
            @AllArguments Object[] parameters,
            @This BaseMocker mocker,
            @Origin Method method
    ) {
        DescriptorsHolder descriptors = descriptorsForStubDelete(parameters, method);
        MappingBuilder mappingBuilder = descriptors.requestMatchingDescriptor
                .toMappingBuilder(new DeleteMappingBuilderStrategy());
        ResponseDefinitionBuilder responseDefinitionBuilder = descriptors.resourceMethodDescriptor
                .toResponseDefinitionBuilder();
        return new DeleteRequestStubber<>(
                mocker.wireMockServer,
                mocker.objectMapper,
                mappingBuilder,
                responseDefinitionBuilder);
    }

    public DeleteRequestVerifier handleVerifyDeleteVerb(
            @AllArguments Object[] parameters,
            @This BaseMocker mocker,
            @Origin Method method
    ) {
        DescriptorsHolder descriptors = descriptorsForVerifyDelete(parameters, method);
        RequestPatternBuilder requestPatternBuilder = descriptors.requestMatchingDescriptor
                .toRequestPatternBuilder(new DeleteRequestedForStrategy());
        return new DeleteRequestVerifier(mocker.wireMockServer, requestPatternBuilder);
    }

    private DescriptorsHolder descriptorsForStubGet(Object[] parameters, Method method) {
        return descriptorsForStub(parameters, method, HttpVerb.GET);
    }

    private DescriptorsHolder descriptorsForStubPost(Object[] parameters, Method method) {
        return descriptorsForStub(parameters, method, HttpVerb.POST);
    }

    private DescriptorsHolder descriptorsForStubPut(Object[] parameters, Method method) {
        return descriptorsForStub(parameters, method, HttpVerb.PUT);
    }

    private DescriptorsHolder descriptorsForStubDelete(Object[] parameters, Method method) {
        return descriptorsForStub(parameters, method, HttpVerb.DELETE);
    }

    private DescriptorsHolder descriptorsForVerifyGet(Object[] parameters, Method method) {
        return descriptorsForVerify(parameters, method, HttpVerb.GET);
    }

    private DescriptorsHolder descriptorsForVerifyPost(Object[] parameters, Method method) {
        return descriptorsForVerify(parameters, method, HttpVerb.POST);
    }

    private DescriptorsHolder descriptorsForVerifyPut(Object[] parameters, Method method) {
        return descriptorsForVerify(parameters, method, HttpVerb.PUT);
    }

    private DescriptorsHolder descriptorsForVerifyDelete(Object[] parameters, Method method) {
        return descriptorsForVerify(parameters, method, HttpVerb.DELETE);
    }

    private DescriptorsHolder descriptorsForStub(Object[] parameters, Method method, HttpVerb verb) {
        return createDescriptors(parameters, method, WireMockStub.class, verb, ENCODED);
    }

    private DescriptorsHolder descriptorsForVerify(Object[] parameters, Method method, HttpVerb verb) {
        return createDescriptors(parameters, method, WireMockVerify.class, verb, UNENCODED);
    }

    private DescriptorsHolder createDescriptors(
            Object[] parameters,
            Method method,
            Class<? extends Annotation> wireMockAnnotation,
            HttpVerb verb,
            RequestMatchingDescriptorFactory.QueryParamEncodingStrategy encodingStrategy
    ) {
        ResourceMethodDescriptor methodDescriptor =
                resourceMethodDescriptorFactory.constructMethodDescriptor(method, wireMockAnnotation);

        methodDescriptor.assertVerb(verb);

        RequestMatchingDescriptor requestMatchingDescriptor = requestMatchingDescriptorFactory
                .createRequestMatchingDescriptor(
                        methodDescriptor.getMethod(),
                        method,
                        parameters,
                        methodDescriptor.createUriBuilder(),
                        encodingStrategy);

        return new DescriptorsHolder(methodDescriptor, requestMatchingDescriptor);
    }

    private static class DescriptorsHolder {
        private final ResourceMethodDescriptor resourceMethodDescriptor;
        private final RequestMatchingDescriptor requestMatchingDescriptor;

        public DescriptorsHolder(
                ResourceMethodDescriptor resourceMethodDescriptor,
                RequestMatchingDescriptor requestMatchingDescriptor
        ) {
            this.resourceMethodDescriptor = resourceMethodDescriptor;
            this.requestMatchingDescriptor = requestMatchingDescriptor;
        }
    }
}
