package jerseywiremock.core;

import javax.ws.rs.Path;
import javax.ws.rs.core.UriBuilder;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class RequestMappingDescriptorFactory {
    public RequestMappingDescriptor createMappingDescriptor(
            Class<?> resourceClass,
            String methodName,
            ParameterDescriptors paramDescriptors
    ) {
        Method method = ReflectionHelper.getMethod(resourceClass, methodName);

        UriBuilder uriBuilder = UriBuilder.fromResource(resourceClass);
        for (Annotation methodAnnotation : method.getDeclaredAnnotations()) {
            if (methodAnnotation.annotationType().equals(Path.class)) {
                uriBuilder.path(method);
            }
        }
        String urlPath = uriBuilder.buildFromMap(paramDescriptors.getPathParams()).toString();

        return new RequestMappingDescriptor(urlPath, paramDescriptors.getQueryParamMatchDescriptors());
    }
}
