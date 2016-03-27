package jerseywiremock.annotations.handler.resourcemethod;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import jerseywiremock.annotations.handler.util.ReflectionHelper;

import javax.ws.rs.Path;
import javax.ws.rs.core.UriBuilder;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;

public class ResourceMethodDescriptor {
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

        UriBuilder uriBuilder = UriBuilder.fromResource(resourceClass);
        for (Annotation methodAnnotation : method.getDeclaredAnnotations()) {
            if (methodAnnotation.annotationType().equals(Path.class)) {
                uriBuilder.path(method);
            }
        }

        return  uriBuilder;
    }

    public ResponseDefinitionBuilder toResponseDefinitionBuilder() {
        ResponseDefinitionBuilder responseDefinitionBuilder = aResponse();

        // TODO: Don't declare content type if void return type from resource method
        // TODO: Add content type based on what was declared by @Produces
        responseDefinitionBuilder.withHeader("Content-Type", "application/json");

        // TODO: Set default status code: usually 200; 201 for POST; 204 for void return types

        return responseDefinitionBuilder;
    }
}
