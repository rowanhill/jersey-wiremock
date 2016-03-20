package jerseywiremock.annotations.handler;

import jerseywiremock.annotations.*;
import jerseywiremock.core.ParameterDescriptors;
import jerseywiremock.core.RequestMappingDescriptor;
import jerseywiremock.core.RequestMappingDescriptorFactory;
import jerseywiremock.core.stub.GetRequestMocker;
import jerseywiremock.core.stub.ListRequestMocker;
import jerseywiremock.core.verify.GetRequestVerifier;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.This;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

public class MockerInvocationHandler {
    private final ParameterDescriptorsFactory parameterDescriptorsFactory;
    private final RequestMappingDescriptorFactory requestMappingDescriptorFactory;

    public MockerInvocationHandler(
            ParameterDescriptorsFactory parameterDescriptorsFactory,
            RequestMappingDescriptorFactory requestMappingDescriptorFactory
    ) {
        this.parameterDescriptorsFactory = parameterDescriptorsFactory;
        this.requestMappingDescriptorFactory = requestMappingDescriptorFactory;
    }

    public <T> GetRequestMocker<T> handleStubGet(
            @AllArguments Object[] parameters,
            @This BaseMocker mocker,
            @Origin Method method
    ) {
        // TODO: Check method is @GET annotated
        MockerMethodDescriptor descriptor = constructMethodDescriptor(parameters, method, WireMockStub.class);
        return new GetRequestMocker<>(mocker.wireMockServer, mocker.objectMapper, descriptor.requestMappingDescriptor);
    }

    public <T> ListRequestMocker<T> handleStubList(
            @AllArguments Object[] parameters,
            @This BaseMocker mocker,
            @Origin Method method
    ) {
        // TODO: Check method is @GET annotated
        MockerMethodDescriptor descriptor = constructMethodDescriptor(parameters, method, WireMockStub.class);
        Collection<T> collection = CollectionFactory.createCollection(descriptor.resourceClass, descriptor.methodName);
        return new ListRequestMocker<>(
                mocker.wireMockServer,
                mocker.objectMapper,
                descriptor.requestMappingDescriptor, collection);
    }

    public GetRequestVerifier handleVerifyGetVerb(
            @AllArguments Object[] parameters,
            @This BaseMocker mocker,
            @Origin Method method
    ) {
        // TODO: Check method is @GET annotated
        MockerMethodDescriptor descriptor = constructMethodDescriptor(parameters, method, WireMockVerify.class);
        return new GetRequestVerifier(mocker.wireMockServer, descriptor.requestMappingDescriptor);
    }

    private MockerMethodDescriptor constructMethodDescriptor(
            Object[] parameters,
            Method method,
            Class<? extends Annotation> wireMockAnnotationType
    ) {
        Class<?> resourceClass = method.getDeclaringClass().getAnnotation(WireMockForResource.class).value();
        String targetMethodName = getTargetMethodName(method, wireMockAnnotationType);
        Annotation[][] mockerMethodParameterAnnotations = method.getParameterAnnotations();
        ParameterDescriptors parameterDescriptors = parameterDescriptorsFactory.createParameterDescriptors(
                parameters,
                mockerMethodParameterAnnotations,
                resourceClass,
                targetMethodName);
        RequestMappingDescriptor mappingDescriptor = requestMappingDescriptorFactory
                .createMappingDescriptor(resourceClass, targetMethodName, parameterDescriptors);
        return new MockerMethodDescriptor(resourceClass, targetMethodName, mappingDescriptor);
    }

    private String getTargetMethodName(Method method, Class<? extends Annotation> wireMockAnnotationType) {
        String methodName;
        if (wireMockAnnotationType == WireMockStub.class) {
            methodName = method.getAnnotation(WireMockStub.class).value();
        } else if (wireMockAnnotationType == WireMockVerify.class) {
            methodName = method.getAnnotation(WireMockVerify.class).value();
        } else {
            throw new RuntimeException("Unexpected annotation: " + wireMockAnnotationType.getSimpleName());
        }
        return methodName;
    }

    private static class MockerMethodDescriptor {
        private final Class<?> resourceClass;
        private final String methodName;
        private final RequestMappingDescriptor requestMappingDescriptor;

        public MockerMethodDescriptor(
                Class<?> resourceClass,
                String methodName,
                RequestMappingDescriptor requestMappingDescriptor
        ) {
            this.resourceClass = resourceClass;
            this.methodName = methodName;
            this.requestMappingDescriptor = requestMappingDescriptor;
        }
    }
}
