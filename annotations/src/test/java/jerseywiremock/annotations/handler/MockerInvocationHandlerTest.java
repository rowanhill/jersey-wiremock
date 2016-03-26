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
import jerseywiremock.core.stub.GetListRequestStubber;
import jerseywiremock.core.stub.GetSingleRequestStubber;
import jerseywiremock.core.stub.PostRequestStubber;
import jerseywiremock.core.verify.GetRequestVerifier;
import jerseywiremock.core.verify.PostRequestVerifier;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.*;

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
    public void handlingStubGetCreatesGetStubber() {
        // given
        stubResourceMethodDescriptorFor(WireMockStub.class);

        // when
        GetSingleRequestStubber<Object> getRequestStubber = handler.handleStubGet(params, testMocker, method);

        // then
        assertThat(getRequestStubber).isNotNull();
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
    public void handlingStubListCreatesListStubber() {
        // given
        stubResourceMethodDescriptorFor(WireMockStub.class);

        // when
        GetListRequestStubber<Object> listRequestStubber = handler.handleStubList(params, testMocker, method);

        // then
        assertThat(listRequestStubber).isNotNull();
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

    @Test
    public void handlingStubPostCreatesPostRequestStubber() {
        // given
        stubResourceMethodDescriptorFor(WireMockStub.class);

        // when
        PostRequestStubber<Object, Object> stubber =
                handler.handleStubPost(params, testMocker, method);

        // then
        assertThat(stubber).isNotNull();
    }

    @Test
    public void handlingVerifyPostCreatesPostRequestVerifier() {
        // given
        stubResourceMethodDescriptorFor(WireMockVerify.class);

        // when
        PostRequestVerifier<Object> verifier =
                handler.handleVerifyPostVerb(params, testMocker, method);

        // then
        assertThat(verifier).isNotNull();
    }

    private void stubResourceMethodDescriptorFor(Class<? extends Annotation> methodAnnotation) {
        when(mockResourceMethodDescriptorFactory.constructMethodDescriptor(method, methodAnnotation))
                .thenReturn(mockDescriptor);
    }

    @SuppressWarnings("unused")
    private static class TestMocker extends BaseMocker {
        private TestMocker(WireMockServer wireMockServer, ObjectMapper objectMapper) {
            super(wireMockServer, objectMapper);
        }

        void method() {}
    }
}