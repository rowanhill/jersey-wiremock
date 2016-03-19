package jerseywiremock.core;

import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.UriBuilder;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class RequestMappingDescriptorFactory {
    public RequestMappingDescriptor createMappingDescriptor(Class<?> resourceClass, String methodName, Map<String, String> paramValues) {
        Method method = ReflectionHelper.getMethod(resourceClass, methodName);

        UriBuilder uriBuilder = UriBuilder.fromResource(resourceClass);
        for (Annotation methodAnnotation : method.getDeclaredAnnotations()) {
            if (methodAnnotation.annotationType().equals(Path.class)) {
                uriBuilder.path(method);
            }
        }
        String urlPath = uriBuilder.buildFromMap(paramValues).toString();

        Map<String, String> queryParams = new HashMap<String, String>();
        for (Annotation[] paramAnnotations : method.getParameterAnnotations()) {
            for (Annotation paramAnnotation : paramAnnotations) {
                if (paramAnnotation instanceof QueryParam) {
                    QueryParam queryParam = (QueryParam) paramAnnotation;
                    String queryParamName = queryParam.value();
                    queryParams.put(queryParamName, paramValues.get(queryParamName));
                }
            }
        }

        return new RequestMappingDescriptor(urlPath, queryParams);
    }

}
