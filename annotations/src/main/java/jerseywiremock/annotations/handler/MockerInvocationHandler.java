package jerseywiremock.annotations.handler;

import jerseywiremock.annotations.*;
import jerseywiremock.core.UrlPathBuilder;
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
    private final ParamMapBuilder paramMapBuilder;
    private final UrlPathBuilder urlPathBuilder;

    public MockerInvocationHandler(ParamMapBuilder paramMapBuilder, UrlPathBuilder urlPathBuilder) {
        this.paramMapBuilder = paramMapBuilder;
        this.urlPathBuilder = urlPathBuilder;
    }

    public <T> GetRequestMocker<T> handleStubGet(@AllArguments Object[] parameters, @This BaseMocker mocker, @Origin Method method) {
        // TODO: Check method is @GET annotated
        MockerMethodDescriptor descriptor = constructUrlPath(parameters, method, WireMockStub.class);
        return new GetRequestMocker<T>(mocker.wireMockServer, mocker.objectMapper, descriptor.urlPath);
    }

    public <T> ListRequestMocker<T> handleStubList(@AllArguments Object[] parameters, @This BaseMocker mocker, @Origin Method method) {
        // TODO: Check method is @GET annotated
        MockerMethodDescriptor descriptor = constructUrlPath(parameters, method, WireMockStub.class);
        Collection<T> collection = CollectionFactory.createCollection(descriptor.resourceClass, descriptor.methodName);
        return new ListRequestMocker<T>(mocker.wireMockServer, mocker.objectMapper, descriptor.urlPath, collection);
    }

    public GetRequestVerifier handleVerifyGetVerb(@AllArguments Object[] parameters, @This BaseMocker mocker, @Origin Method method) {
        // TODO: Check method is @GET annotated
        MockerMethodDescriptor descriptor = constructUrlPath(parameters, method, WireMockVerify.class);
        return new GetRequestVerifier(mocker.wireMockServer, descriptor.urlPath);
    }

    private MockerMethodDescriptor constructUrlPath(Object[] parameters, Method method, Class<? extends Annotation> wireMockAnnotationType) {
        Class<?> resourceClass = method.getDeclaringClass().getAnnotation(WireMockForResource.class).value();
        String methodName = getTargetMethodName(method, wireMockAnnotationType);
        Map<String, Object> paramMap = paramMapBuilder.getParamMap(parameters, resourceClass, methodName);
        return new MockerMethodDescriptor(resourceClass, methodName, urlPathBuilder.buildUrlPath(resourceClass, methodName, paramMap));
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
        private final String urlPath;

        public MockerMethodDescriptor(Class<?> resourceClass, String methodName, String urlPath) {
            this.resourceClass = resourceClass;
            this.methodName = methodName;
            this.urlPath = urlPath;
        }
    }
}
