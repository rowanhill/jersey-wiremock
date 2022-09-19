package io.jerseywiremock.annotations.factory;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.common.collect.ImmutableList;

import io.jerseywiremock.core.stub.request.GetSingleRequestStubber;
import io.jerseywiremock.core.verify.GetRequestVerifier;

@ExtendWith(MockitoExtension.class)
public class MockerTypeCheckerTest {
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
        assertThrows(Exception.class, () -> checker.checkReturnTypes(TestInterface.class));
    }

    @Test
    public void methodsReturningMockersDoNotCauseException() throws Exception {
        // given
        Method returnsStubber = TestInterface.class.getMethod("returnsStubber");
        when(mockMethodSelector.getMethodsForType(TestInterface.class)).thenReturn(ImmutableList.of(returnsStubber));

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
        GetSingleRequestStubber<Integer> returnsStubber();
        GetRequestVerifier returnsVerifier();
    }
}