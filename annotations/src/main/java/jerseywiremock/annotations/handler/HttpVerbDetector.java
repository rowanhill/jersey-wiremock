package jerseywiremock.annotations.handler;

import jerseywiremock.core.ReflectionHelper;

import javax.ws.rs.GET;
import java.lang.reflect.Method;

public class HttpVerbDetector {
    HttpVerb getVerbFromAnnotation(Class<?> resourceClass, String methodName) {
        Method method = ReflectionHelper.getMethod(resourceClass, methodName);

        for (HttpVerb verb : HttpVerb.values()) {
            if (method.getAnnotation(verb.getAnnotation()) != null) {
                return verb;
            }
        }
        throw new RuntimeException("Could not determine HTTP verb for " + methodName);
    }
}
