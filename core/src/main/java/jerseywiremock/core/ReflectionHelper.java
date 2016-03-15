package jerseywiremock.core;

import java.lang.reflect.Method;

public class ReflectionHelper {
    public static Method getMethod(Class<?> clazz, String methodName) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        throw new RuntimeException("No method named " + methodName + " on " + clazz.getSimpleName());
    }
}
