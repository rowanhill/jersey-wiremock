package jerseywiremock.annotations.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import jerseywiremock.annotations.WireMockStub;
import jerseywiremock.annotations.WireMockVerify;
import jerseywiremock.annotations.handler.requestmapping.RequestMappingDescriptor;
import jerseywiremock.annotations.handler.requestmapping.RequestMappingDescriptorFactory;
import jerseywiremock.annotations.handler.resourcemethod.HttpVerb;
import jerseywiremock.annotations.handler.resourcemethod.ResourceMethodDescriptor;
import jerseywiremock.annotations.handler.resourcemethod.ResourceMethodDescriptorFactory;
import jerseywiremock.annotations.handler.util.CollectionFactory;
import jerseywiremock.core.stub.GetRequestMocker;
import jerseywiremock.core.stub.ListRequestMocker;
import jerseywiremock.core.verify.GetRequestVerifier;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MockerInvocationHandlerTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private ResourceMethodDescriptorFactory mockResourceMethodDescriptorFactory;
    @Mock
    private RequestMappingDescriptorFactory mockRequestMappingDescriptorFactory;
    @Mock
    private CollectionFactory mockCollectionFactory;
    @InjectMocks
    private MockerInvocationHandler handler;

    private Object[] params;
    private TestMocker testMocker;
    private Method method;
    private ResourceMethodDescriptor mockDescriptor;

    @Before
    public void setUp() throws Exception {
        params = new Object[]{};
        testMocker = new TestMocker(null, null);
        method = TestMocker.class.getDeclaredMethod("method");

        mockDescriptor = mock(ResourceMethodDescriptor.class);
        when(mockDescriptor.getMethodName()).thenReturn("method");

        RequestMappingDescriptor mockMappingDescriptor = mock(RequestMappingDescriptor.class);
        when(mockRequestMappingDescriptorFactory.createMappingDescriptor(mockDescriptor, method, params))
                .thenReturn(mockMappingDescriptor);
    }

    @Test
    public void handlingStubGetCreatesGetMocker() {
        // given
        stubResourceMethodDescriptorFor(WireMockStub.class);

        // when
        GetRequestMocker<Object> getRequestMocker = handler.handleStubGet(params, testMocker, method);

        // then
        assertThat(getRequestMocker).isNotNull();
    }

    @Test
    public void handlingStubGetForMethodThatServicesDifferentVerbThrowsException() {
        // given
        stubResourceMethodDescriptorFor(WireMockStub.class);
        RuntimeException rte = new RuntimeException("test");
        doThrow(rte).when(mockDescriptor).assertVerb(HttpVerb.GET);

        // when
        expectedException.expect(is(rte));
        handler.handleStubGet(params, testMocker, method);
    }

    @Test
    public void handlingStubListCreatesListMocker() {
        // given
        stubResourceMethodDescriptorFor(WireMockStub.class);

        // when
        ListRequestMocker<Object> listRequestMocker = handler.handleStubList(params, testMocker, method);

        // then
        assertThat(listRequestMocker).isNotNull();
    }

    @Test
    public void handlingStubListForMethodThatServicesDifferentVerbThrowsException() {
        // given
        stubResourceMethodDescriptorFor(WireMockStub.class);
        RuntimeException rte = new RuntimeException("test");
        doThrow(rte).when(mockDescriptor).assertVerb(HttpVerb.GET);

        // when
        expectedException.expect(is(rte));
        handler.handleStubList(params, testMocker, method);
    }

    @Test
    public void handlingVerifyGetVerbCreatesGetVerifier() {
        // given
        stubResourceMethodDescriptorFor(WireMockVerify.class);

        // when
        GetRequestVerifier getRequestVerifier = handler.handleVerifyGetVerb(params, testMocker, method);

        // then
        assertThat(getRequestVerifier).isNotNull();
    }

    @Test
    public void handlingVerifyGerVerbForMethodThatServicesDifferentVerbThrowsException() {
        // given
        stubResourceMethodDescriptorFor(WireMockVerify.class);
        RuntimeException rte = new RuntimeException("test");
        doThrow(rte).when(mockDescriptor).assertVerb(HttpVerb.GET);

        // when
        expectedException.expect(is(rte));
        handler.handleVerifyGetVerb(params, testMocker, method);
    }

    private void stubResourceMethodDescriptorFor(Class<? extends Annotation> methodAnnotation) {
        when(mockResourceMethodDescriptorFactory.constructMethodDescriptor(method, methodAnnotation))
                .thenReturn(mockDescriptor);
    }

    private static class TestMocker extends BaseMocker {
        private TestMocker(WireMockServer wireMockServer, ObjectMapper objectMapper) {
            super(wireMockServer, objectMapper);
        }

        void method() {}
    }
}