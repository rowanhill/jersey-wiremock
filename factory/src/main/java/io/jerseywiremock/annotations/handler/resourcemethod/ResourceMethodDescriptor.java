package io.jerseywiremock.annotations.handler.resourcemethod;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import io.jerseywiremock.annotations.handler.util.ReflectionHelper;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.UriBuilder;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;

public class ResourceMethodDescriptor {
    private static final String FALLBACK_CONTENT_TYPE = "application/json";
    public static final String ROOT_PATH = "/";
    private final Class<?> resourceClass;
    private final String methodName;
    private final HttpVerb verb;

    ResourceMethodDescriptor(
            Class<?> resourceClass,
            String methodName,
            HttpVerb verb
    ) {
        this.resourceClass = resourceClass;
        this.methodName = methodName;
        this.verb = verb;
    }

    /**
     * The class of the Jersey resource being described
     */
    public Class<?> getResourceClass() {
        return resourceClass;
    }

    /**
     * The name of the method being described on the Jersey resource
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * @return The method on the Jersey resource being described, looked up by reflection
     */
    public Method getMethod() {
        return ReflectionHelper.getMethod(resourceClass, methodName);
    }

    /**
     * Throws a RuntimeException if the resource method was not annotated with the expected HTTP verb
     */
    public void assertVerb(HttpVerb verb) {
        if (this.verb != verb) {
            throw new RuntimeException("Expected " + methodName + " to be annotated with @"
                    + verb.getAnnotation().getSimpleName());
        }
    }

    public UriBuilder createUriBuilder() {
        Method method = ReflectionHelper.getMethod(resourceClass, methodName);

        UriBuilder uriBuilder = UriBuilder.fromPath(ROOT_PATH).path(resourceClass);
        for (Annotation methodAnnotation : method.getDeclaredAnnotations()) {
            if (methodAnnotation.annotationType().equals(Path.class)) {
                uriBuilder.path(method);
            }
        }
        return  uriBuilder;
    }

    public ResponseDefinitionBuilder toResponseDefinitionBuilder() {
        ResponseDefinitionBuilder responseDefinitionBuilder = aResponse();
        Method method = getMethod();
        if (!method.getReturnType().equals(Void.TYPE)) {
            responseDefinitionBuilder.withHeader("Content-Type", computeContentType());
        }
        if (method.getReturnType().equals(Void.TYPE)) {
            responseDefinitionBuilder.withStatus(204);
        } else {
            if (verb == HttpVerb.POST) {
                responseDefinitionBuilder.withStatus(201);
            }
        }
        return responseDefinitionBuilder;
    }

    public String computeContentType() {
        Produces producesAnnotation = Optional.ofNullable(getMethod().getAnnotation(Produces.class))
                .orElseGet(() -> getResourceClass().getAnnotation(Produces.class));
        return producesAnnotation != null ? producesAnnotation.value()[0] : FALLBACK_CONTENT_TYPE;
    }

}
