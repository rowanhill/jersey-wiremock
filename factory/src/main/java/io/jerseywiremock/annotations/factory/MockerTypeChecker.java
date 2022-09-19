package io.jerseywiremock.annotations.factory;

import io.jerseywiremock.core.stub.request.BaseRequestStubber;
import io.jerseywiremock.core.verify.BaseRequestVerifier;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

class MockerTypeChecker {
    private final MockerMethodSelector methodSelector;

    MockerTypeChecker(MockerMethodSelector methodSelector) {
        this.methodSelector = methodSelector;
    }

    <T> void checkReturnTypes(Class<T> mockerType) {
        List<Method> methods = methodSelector.getMethodsForType(mockerType);

        List<Method> badMethods = selectBadMethods(methods);

        if (!badMethods.isEmpty()) {
            throwExceptionFor(badMethods);
        }
    }

    private List<Method> selectBadMethods(List<Method> methods) {
        return methods.stream()
                .filter(method -> {
                    Class<?> returnType = method.getReturnType();
                    return !(isStubber(returnType) || isVerifier(returnType));
                })
                .collect(Collectors.toList());
    }

    private boolean isStubber(Class<?> returnType) {
        return BaseRequestStubber.class.isAssignableFrom(returnType);
    }

    private boolean isVerifier(Class<?> returnType) {
        return BaseRequestVerifier.class.isAssignableFrom(returnType);
    }

    private void throwExceptionFor(List<Method> badMethods) {
        StringBuilder builder = new StringBuilder();
        builder.append("All methods must return request stubbers or verifiers. The following methods do not:\n");
        for (Method badMethod : badMethods) {
            builder.append("\t").append(badMethod).append("\n");
        }
        throw new RuntimeException(builder.toString());
    }
}
