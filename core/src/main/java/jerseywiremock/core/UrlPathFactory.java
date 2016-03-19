package jerseywiremock.core;

import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.UriBuilder;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

public class UrlPathFactory {
    public String createUrlPath(Class<?> resourceClass, String methodName, Map<String, Object> paramValues) {
        UriBuilder uriBuilder = UriBuilder.fromResource(resourceClass);

        Method method = ReflectionHelper.getMethod(resourceClass, methodName);

        for (Annotation methodAnnotation : method.getDeclaredAnnotations()) {
            if (methodAnnotation.annotationType().equals(Path.class)) {
                uriBuilder.path(method);
            }
        }
        for (Annotation[] paramAnnotations : method.getParameterAnnotations()) {
            for (Annotation paramAnnotation : paramAnnotations) {
                if (paramAnnotation instanceof QueryParam) {
                    QueryParam queryParam = (QueryParam) paramAnnotation;
                    String queryParamName = queryParam.value();
                    uriBuilder.queryParam(queryParamName, paramValues.get(queryParamName));
                }
            }
        }

        return uriBuilder.buildFromMap(paramValues).toString();
    }
}
