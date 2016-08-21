package io.jerseywiremock.annotations.factory;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;

class MockerMethodSelector {
    List<Method> getMethodsForType(Class<?> mockerType) {
        List<Method> methods = new LinkedList<>();
        for (Method method : mockerType.getDeclaredMethods()) {
            if (Modifier.isAbstract(method.getModifiers())) {
                methods.add(method);
            }
        }
        return methods;
    }
}
