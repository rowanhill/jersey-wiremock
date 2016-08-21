package io.jerseywiremock.annotations.handler.resourcemethod;

import io.jerseywiremock.annotations.handler.util.ReflectionHelper;

import java.lang.reflect.Method;

public class HttpVerbDetector {
    public HttpVerb getVerbFromAnnotation(Class<?> resourceClass, String methodName) {
        Method method = ReflectionHelper.getMethod(resourceClass, methodName);

        for (HttpVerb verb : HttpVerb.values()) {
            if (method.getAnnotation(verb.getAnnotation()) != null) {
                return verb;
            }
        }
        throw new RuntimeException("Could not determine HTTP verb for " + methodName);
    }
}
