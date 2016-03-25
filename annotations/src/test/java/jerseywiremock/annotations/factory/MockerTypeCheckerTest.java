package jerseywiremock.annotations.factory;

import com.google.common.collect.ImmutableList;
import jerseywiremock.core.stub.GetRequestMocker;
import jerseywiremock.core.verify.GetRequestVerifier;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Method;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MockerTypeCheckerTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private MockerMethodSelector mockMethodSelector;
    @InjectMocks
    private MockerTypeChecker checker;

    @Test
    public void methodsWithUnexpectedReturnTypesCauseException() throws Exception {
        // given
        Method returnsInt = TestInterface.class.getMethod("returnsInt");
        Method returnsString = TestInterface.class.getMethod("returnsString");
        when(mockMethodSelector.getMethodsForType(TestInterface.class))
                .thenReturn(ImmutableList.of(returnsInt, returnsString));

        // when
        expectedException.expectMessage("All methods must return request mockers or verifiers");
        expectedException.expectMessage("returnsInt");
        expectedException.expectMessage("returnsString");
        checker.checkReturnTypes(TestInterface.class);
    }

    @Test
    public void methodsReturningMockersDoNotCauseException() throws Exception {
        // given
        Method returnsMocker = TestInterface.class.getMethod("returnsMocker");
        when(mockMethodSelector.getMethodsForType(TestInterface.class)).thenReturn(ImmutableList.of(returnsMocker));

        // when
        checker.checkReturnTypes(TestInterface.class);
    }

    @Test
    public void methodsReturningVerifiersDoNotCauseException() throws Exception {
        // given
        Method returnsVerifier = TestInterface.class.getMethod("returnsVerifier");
        when(mockMethodSelector.getMethodsForType(TestInterface.class)).thenReturn(ImmutableList.of(returnsVerifier));

        // when
        checker.checkReturnTypes(TestInterface.class);
    }

    private interface TestInterface {
        int returnsInt();
        String returnsString();
        GetRequestMocker<Integer> returnsMocker();
        GetRequestVerifier returnsVerifier();
    }
}