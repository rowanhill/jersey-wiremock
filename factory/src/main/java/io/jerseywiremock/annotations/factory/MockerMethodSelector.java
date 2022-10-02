package io.jerseywiremock.annotations.factory;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

class MockerMethodSelector {
    List<Method> getMethodsForType(Class<?> mockerType) {
        return Arrays.stream(mockerType.getDeclaredMethods())
                .filter(method -> Modifier.isAbstract(method.getModifiers()))
                .collect(Collectors.toList());
    }
}
