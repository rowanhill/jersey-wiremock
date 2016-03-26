package jerseywiremock.annotations.factory;

import jerseywiremock.core.stub.GetRequestStubber;
import jerseywiremock.core.verify.GetRequestVerifier;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

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
        GetRequestStubber<Integer> getRequestStubber();
        GetRequestVerifier getRequestVerifier();
    }

    @SuppressWarnings("unused")
    private static abstract class TestClass {
        abstract GetRequestStubber<Integer> getRequestStubber();

        GetRequestVerifier getRequestVerifier() {
            return null;
        }
    }
}