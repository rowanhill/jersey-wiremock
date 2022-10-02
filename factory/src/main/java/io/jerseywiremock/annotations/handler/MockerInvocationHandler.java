package io.jerseywiremock.annotations.handler;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;

import io.jerseywiremock.annotations.WireMockStub;
import io.jerseywiremock.annotations.WireMockVerify;
import io.jerseywiremock.annotations.handler.requestmatching.RequestMatchingDescriptor;
import io.jerseywiremock.annotations.handler.requestmatching.RequestMatchingDescriptorFactory;
import io.jerseywiremock.annotations.handler.requestmatching.stubverbs.DeleteMappingBuilderStrategy;
import io.jerseywiremock.annotations.handler.requestmatching.stubverbs.GetMappingBuilderStrategy;
import io.jerseywiremock.annotations.handler.requestmatching.stubverbs.PostMappingBuilderStrategy;
import io.jerseywiremock.annotations.handler.requestmatching.stubverbs.PutMappingBuilderStrategy;
import io.jerseywiremock.annotations.handler.requestmatching.verifyverbs.DeleteRequestedForStrategy;
import io.jerseywiremock.annotations.handler.requestmatching.verifyverbs.GetRequestedForStrategy;
import io.jerseywiremock.annotations.handler.requestmatching.verifyverbs.PostRequestedForStrategy;
import io.jerseywiremock.annotations.handler.requestmatching.verifyverbs.PutRequestedForStrategy;
import io.jerseywiremock.annotations.handler.resourcemethod.HttpVerb;
import io.jerseywiremock.annotations.handler.resourcemethod.ResourceMethodDescriptor;
import io.jerseywiremock.annotations.handler.resourcemethod.ResourceMethodDescriptorFactory;
import io.jerseywiremock.annotations.handler.util.CollectionFactory;
import io.jerseywiremock.core.stub.request.*;
import io.jerseywiremock.core.verify.DeleteRequestVerifier;
import io.jerseywiremock.core.verify.GetRequestVerifier;
import io.jerseywiremock.core.verify.PostRequestVerifier;
import io.jerseywiremock.core.verify.PutRequestVerifier;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.This;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;

import static io.jerseywiremock.annotations.handler.requestmatching.RequestMatchingDescriptorFactory.QueryParamEncodingStrategy.ENCODED;
import static io.jerseywiremock.annotations.handler.requestmatching.RequestMatchingDescriptorFactory.QueryParamEncodingStrategy.UNENCODED;

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
        String contentType = descriptors.resourceMethodDescriptor.computeContentType();
        Serializer serializer = mocker.serializers.getSerializerByContentType(contentType);

        return new GetSingleRequestStubber<>(
                mocker.wireMock,
                serializer,
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
                descriptors.resourceMethodDescriptor.getMethodName()
        );
        String contentType = descriptors.resourceMethodDescriptor.computeContentType();
        Serializer serializer = mocker.serializers.getSerializerByContentType(contentType);
        return new GetListRequestStubber<>(
                mocker.wireMock,
                serializer,
                mappingBuilder,
                responseDefinitionBuilder,
                collection
        );
    }

    public GetRequestVerifier handleVerifyGetVerb(
            @AllArguments Object[] parameters,
            @This BaseMocker mocker,
            @Origin Method method
    ) {
        DescriptorsHolder descriptors = descriptorsForVerifyGet(parameters, method);
        RequestPatternBuilder requestPatternBuilder = descriptors.requestMatchingDescriptor
                .toRequestPatternBuilder(new GetRequestedForStrategy());
        return new GetRequestVerifier(mocker.wireMock, requestPatternBuilder);
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
        String contentType = descriptors.resourceMethodDescriptor.computeContentType();
        Serializer serializer = mocker.serializers.getSerializerByContentType(contentType);
        return new PostRequestStubber<>(
                mocker.wireMock,
                serializer,
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
        String contentType = descriptors.resourceMethodDescriptor.computeContentType();
        Serializer serializer = mocker.serializers.getSerializerByContentType(contentType);
        return new PostRequestVerifier<>(mocker.wireMock, serializer, requestPatternBuilder);
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
        String contentType = descriptors.resourceMethodDescriptor.computeContentType();
        Serializer serializer = mocker.serializers.getSerializerByContentType(contentType);
        return new PutRequestStubber<>(
                mocker.wireMock,
                serializer,
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
        String contentType = descriptors.resourceMethodDescriptor.computeContentType();
        Serializer serializer = mocker.serializers.getSerializerByContentType(contentType);
        return new PutRequestVerifier<>(mocker.wireMock, serializer, requestPatternBuilder);
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
        String contentType = descriptors.resourceMethodDescriptor.computeContentType();
        Serializer serializer = mocker.serializers.getSerializerByContentType(contentType);
        return new DeleteRequestStubber<>(
                mocker.wireMock,
                serializer,
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
        return new DeleteRequestVerifier(mocker.wireMock, requestPatternBuilder);
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
