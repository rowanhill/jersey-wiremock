package io.jerseywiremock.annotations.handler.util;

import java.lang.reflect.Method;
import java.util.*;

public class CollectionFactory {
    public <T> Collection<T> createCollection(Class<?> resourceClass, String methodName) {
        Method method = ReflectionHelper.getMethod(resourceClass, methodName);

        Class<?> returnType = method.getReturnType();
        if (Collection.class.isAssignableFrom(returnType)) {
            return createCollection(returnType);
        } else {
            throw new RuntimeException(method.getDeclaringClass().getSimpleName() + "#" + methodName +
                    " does not return Collection type; it returns " + returnType.getSimpleName());
        }
    }

    private <T> Collection<T> createCollection(Class<?> returnType) {
        if (List.class.isAssignableFrom(returnType)) {
            return new ArrayList<>();
        } else if (Set.class.isAssignableFrom(returnType)) {
            return new HashSet<>();
        } else if (Collection.class.equals(returnType)) {
            return new ArrayList<>();
        } else {
            throw new RuntimeException("Cannot create collection for type " + returnType.getSimpleName());
        }
    }
}
