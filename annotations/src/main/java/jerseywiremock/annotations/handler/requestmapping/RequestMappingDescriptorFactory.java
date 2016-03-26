package jerseywiremock.annotations.handler.requestmapping;

import jerseywiremock.annotations.handler.requestmapping.paramdescriptors.ParameterDescriptors;
import jerseywiremock.annotations.handler.requestmapping.paramdescriptors.ParameterDescriptorsFactory;
import jerseywiremock.annotations.handler.resourcemethod.ResourceMethodDescriptor;
import jerseywiremock.annotations.handler.util.ReflectionHelper;

import javax.ws.rs.Path;
import javax.ws.rs.core.UriBuilder;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class RequestMappingDescriptorFactory {
    private final ParameterDescriptorsFactory parameterDescriptorsFactory;

    public RequestMappingDescriptorFactory(ParameterDescriptorsFactory parameterDescriptorsFactory) {
        this.parameterDescriptorsFactory = parameterDescriptorsFactory;
    }

    public RequestMappingDescriptor createMappingDescriptor(
            ResourceMethodDescriptor resourceMethodDescriptor,
            Method mockerMethod,
            Object[] parameters
    ) {
        Class<?> resourceClass = resourceMethodDescriptor.getResourceClass();
        String resourceMethodName = resourceMethodDescriptor.getMethodName();
        Annotation[][] mockerMethodParameterAnnotations = mockerMethod.getParameterAnnotations();
        ParameterDescriptors paramDescriptors = parameterDescriptorsFactory.createParameterDescriptors(
                parameters,
                mockerMethodParameterAnnotations,
                resourceClass,
                resourceMethodName);

        Method method = ReflectionHelper.getMethod(resourceClass, resourceMethodName);

        UriBuilder uriBuilder = UriBuilder.fromResource(resourceClass);
        for (Annotation methodAnnotation : method.getDeclaredAnnotations()) {
            if (methodAnnotation.annotationType().equals(Path.class)) {
                uriBuilder.path(method);
            }
        }
        String urlPath = uriBuilder.buildFromMap(paramDescriptors.getPathParams()).toString();

        return new RequestMappingDescriptor(
                urlPath,
                paramDescriptors.getQueryParamMatchingStrategies(),
                paramDescriptors.getRequestBodyMatchingStrategy());
    }
}
