package io.jerseywiremock.annotations.handler.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

public class ReflectionHelperTest {
    @Test
    public void methodCanBeRetrievedFromClassByName() {
        // when
        Method method = ReflectionHelper.getMethod(ReflectionTestClass.class, "someMethod");

        // then
        assertThat(method.getName()).isEqualTo("someMethod");
    }

    @Test
    public void exceptionIsRaisedIfMethodNameDoesNotExist() {
        // when
        assertThrows(Exception.class, () -> ReflectionHelper.getMethod(ReflectionTestClass.class, "notAMethod"));
    }

    @SuppressWarnings("unused")
    private static class ReflectionTestClass {
        void someMethod(int dummyParam) {}
    }
}