package io.jerseywiremock.annotations.handler.resourcemethod;

import io.jerseywiremock.annotations.WireMockForResource;
import io.jerseywiremock.annotations.WireMockStub;
import io.jerseywiremock.annotations.WireMockVerify;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class ResourceMethodDescriptorFactory {
    private final HttpVerbDetector verbDetector;

    public ResourceMethodDescriptorFactory(
            HttpVerbDetector verbDetector
    ) {
        this.verbDetector = verbDetector;
    }

    public ResourceMethodDescriptor constructMethodDescriptor(
            Method method,
            Class<? extends Annotation> wireMockAnnotationType
    ) {
        Class<?> resourceClass = method.getDeclaringClass().getAnnotation(WireMockForResource.class).value();
        String targetMethodName = getTargetMethodName(method, wireMockAnnotationType);
        HttpVerb verb = verbDetector.getVerbFromAnnotation(resourceClass, targetMethodName);
        return new ResourceMethodDescriptor(resourceClass, targetMethodName, verb);
    }

    private String getTargetMethodName(Method method, Class<? extends Annotation> wireMockAnnotationType) {
        String methodName;
        if (wireMockAnnotationType == WireMockStub.class) {
            WireMockStub annotation = method.getAnnotation(WireMockStub.class);
            if (annotation == null) {
                throw new RuntimeException("Expected " + method.getName() +
                        " to be annotated with @WireMockStub, but it was not");
            }
            methodName = annotation.value();
        } else if (wireMockAnnotationType == WireMockVerify.class) {
            WireMockVerify annotation = method.getAnnotation(WireMockVerify.class);
            if (annotation == null) {
                throw new RuntimeException("Expected " + method.getName() +
                        " to be annotated with @WireMockVerify, but it was not");
            }
            methodName = annotation.value();
        } else {
            throw new RuntimeException("Unexpected annotation: " + wireMockAnnotationType.getSimpleName());
        }
        return methodName;
    }
}
