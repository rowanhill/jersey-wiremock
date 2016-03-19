package jerseywiremock.annotations.handler;

import jerseywiremock.annotations.*;
import jerseywiremock.core.UrlPathBuilder;
import jerseywiremock.core.stub.GetRequestMocker;
import jerseywiremock.core.stub.ListRequestMocker;
import jerseywiremock.core.verify.GetRequestVerifier;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.This;

import java.lang.reflect.Method;
import java.util.*;

public class MockerInvocationHandler {
    private final ParamMapBuilder paramMapBuilder;

    public MockerInvocationHandler(ParamMapBuilder paramMapBuilder) {
        this.paramMapBuilder = paramMapBuilder;
    }

    public <T> GetRequestMocker<T> handleStubGet(@AllArguments Object[] parameters, @This BaseMocker mocker, @Origin Method method) {
        Class<?> resourceClass = method.getDeclaringClass().getAnnotation(WireMockForResource.class).value();
        String methodName = method.getAnnotation(WireMockStub.class).value();

        // TODO: Check method is @GET annotated
        Map<String, Object> paramMap = paramMapBuilder.getParamMap(parameters, resourceClass, methodName);
        String urlPath = UrlPathBuilder.buildUrlPath(resourceClass, methodName, paramMap);
        return new GetRequestMocker<T>(mocker.wireMockServer, mocker.objectMapper, urlPath);
    }

    public <T> ListRequestMocker<T> handleStubList(@AllArguments Object[] parameters, @This BaseMocker mocker, @Origin Method method) {
        Class<?> resourceClass = method.getDeclaringClass().getAnnotation(WireMockForResource.class).value();
        String methodName = method.getAnnotation(WireMockStub.class).value();

        // TODO: Check method is @GET annotated
        Map<String, Object> paramMap = paramMapBuilder.getParamMap(parameters, resourceClass, methodName);
        String urlPath = UrlPathBuilder.buildUrlPath(resourceClass, methodName, paramMap);
        Collection<T> collection = CollectionFactory.createCollection(resourceClass, methodName);
        return new ListRequestMocker<T>(mocker.wireMockServer, mocker.objectMapper, urlPath, collection);
    }

    public GetRequestVerifier handleVerifyGetVerb(@AllArguments Object[] parameters, @This BaseMocker mocker, @Origin Method method) {
        Class<?> resourceClass = method.getDeclaringClass().getAnnotation(WireMockForResource.class).value();
        String methodName = method.getAnnotation(WireMockVerify.class).value();

        // TODO: Check method is @GET annotated
        Map<String, Object> paramMap = paramMapBuilder.getParamMap(parameters, resourceClass, methodName);
        String urlPath = UrlPathBuilder.buildUrlPath(resourceClass, methodName, paramMap);
        return new GetRequestVerifier(mocker.wireMockServer, urlPath);
    }
}
