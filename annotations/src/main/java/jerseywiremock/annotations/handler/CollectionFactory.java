package jerseywiremock.annotations.handler;

import jerseywiremock.core.ReflectionHelper;

import java.lang.reflect.Method;
import java.util.*;

class CollectionFactory {
    static <T> Collection<T> createCollection(Class<?> resourceClass, String methodName) {
        Method method = ReflectionHelper.getMethod(resourceClass, methodName);

        Class<?> returnType = method.getReturnType();
        if (returnType.isAssignableFrom(Collection.class)) {
            return createCollection(returnType);
        } else {
            throw new RuntimeException(method.getDeclaringClass().getSimpleName() + "#" + methodName +
                    " does not return Collection type; it returns " + returnType.getSimpleName());
        }
    }

    private static <T> Collection<T> createCollection(Class<?> returnType) {
        if (returnType.isAssignableFrom(List.class)) {
            return new ArrayList<T>();
        } else if (returnType.isAssignableFrom(Set.class)) {
            return new HashSet<T>();
        } else if (returnType.equals(Collection.class)) {
            return new ArrayList<T>();
        } else {
            throw new RuntimeException("Cannot create collection for type " + returnType.getSimpleName());
        }
    }
}
