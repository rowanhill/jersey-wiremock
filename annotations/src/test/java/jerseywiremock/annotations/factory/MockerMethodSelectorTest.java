package jerseywiremock.annotations.factory;

import jerseywiremock.core.stub.GetSingleRequestStubber;
import jerseywiremock.core.verify.EmptyRequestVerifier;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MockerMethodSelectorTest {
    @Test
    public void allMethodsFromInterfaceAreSelected() {
        // given
        MockerMethodSelector selector = new MockerMethodSelector();

        // when
        List<Method> methods = selector.getMethodsForType(TestInterface.class);

        // then
        assertThat(methods).extracting("name").containsOnly("getRequestStubber", "getRequestVerifier");
    }

    @Test
    public void allAbstractMethodsFromAbstractClassAreSelected() {
        // given
        MockerMethodSelector selector = new MockerMethodSelector();

        // when
        List<Method> methods = selector.getMethodsForType(TestClass.class);

        // then
        assertThat(methods).extracting("name").containsOnly("getRequestStubber");
    }

    @SuppressWarnings("unused")
    private interface TestInterface {
        GetSingleRequestStubber<Integer> getRequestStubber();
        EmptyRequestVerifier getRequestVerifier();
    }

    @SuppressWarnings("unused")
    private static abstract class TestClass {
        abstract GetSingleRequestStubber<Integer> getRequestStubber();

        EmptyRequestVerifier getRequestVerifier() {
            return null;
        }
    }
}