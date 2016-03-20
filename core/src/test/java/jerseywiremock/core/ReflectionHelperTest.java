package jerseywiremock.core;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

public class ReflectionHelperTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

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
        expectedException.expectMessage("No method named notAMethod on ReflectionTestClass");
         ReflectionHelper.getMethod(ReflectionTestClass.class, "notAMethod");
    }

    @SuppressWarnings("unused")
    private static class ReflectionTestClass {
        void someMethod(int dummyParam) {}
    }
}