package jerseywiremock.annotations.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import jerseywiremock.annotations.WireMockStub;
import jerseywiremock.annotations.WireMockVerify;
import jerseywiremock.core.RequestMappingDescriptor;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MockerInvocationHandlerTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private ResourceMethodDescriptorFactory mockResourceMethodDescriptorFactory;
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
        RequestMappingDescriptor mockMappingDescriptor = mock(RequestMappingDescriptor.class);
        when(mockMappingDescriptor.getUrlPath()).thenReturn("url");
        when(mockDescriptor.getRequestMappingDescriptor()).thenReturn(mockMappingDescriptor);
        when(mockDescriptor.getMethodName()).thenReturn("method");
    }

    @Test
    public void handlingStubGetCreatesGetMocker() {
        // given
        stubResourceMethodDescriptorFor(WireMockStub.class);
        when(mockDescriptor.getVerb()).thenReturn(HttpVerb.GET);

        // when
        GetRequestMocker<Object> getRequestMocker = handler.handleStubGet(params, testMocker, method);

        // then
        assertThat(getRequestMocker).isNotNull();
    }

    @Test
    public void handlingStubGetForMethodThatServicesDifferentVerbThrowsException() {
        // given
        stubResourceMethodDescriptorFor(WireMockStub.class);
        when(mockDescriptor.getVerb()).thenReturn(HttpVerb.POST);

        // when
        expectedException.expectMessage("Expected method to be annotated with @GET");
        handler.handleStubGet(params, testMocker, method);
    }

    @Test
    public void handlingStubListCreatesListMocker() {
        // given
        stubResourceMethodDescriptorFor(WireMockStub.class);
        when(mockDescriptor.getVerb()).thenReturn(HttpVerb.GET);

        // when
        ListRequestMocker<Object> listRequestMocker = handler.handleStubList(params, testMocker, method);

        // then
        assertThat(listRequestMocker).isNotNull();
    }

    @Test
    public void handlingStubListForMethodThatServicesDifferentVerbThrowsException() {
        // given
        stubResourceMethodDescriptorFor(WireMockStub.class);
        when(mockDescriptor.getVerb()).thenReturn(HttpVerb.POST);

        // when
        expectedException.expectMessage("Expected method to be annotated with @GET");
        handler.handleStubList(params, testMocker, method);
    }

    @Test
    public void handlingVerifyGetVerbCreatesGetVerifier() {
        // given
        stubResourceMethodDescriptorFor(WireMockVerify.class);
        when(mockDescriptor.getVerb()).thenReturn(HttpVerb.GET);

        // when
        GetRequestVerifier getRequestVerifier = handler.handleVerifyGetVerb(params, testMocker, method);

        // then
        assertThat(getRequestVerifier).isNotNull();
    }

    @Test
    public void handlingVerifyGerVerbForMethodThatServicesDifferentVerbThrowsException() {
        // given
        stubResourceMethodDescriptorFor(WireMockVerify.class);
        when(mockDescriptor.getVerb()).thenReturn(HttpVerb.POST);

        // when
        expectedException.expectMessage("Expected method to be annotated with @GET");
        handler.handleVerifyGetVerb(params, testMocker, method);
    }

    private void stubResourceMethodDescriptorFor(Class<? extends Annotation> methodAnnotation) {
        when(mockResourceMethodDescriptorFactory.constructMethodDescriptor(params, method, methodAnnotation))
                .thenReturn(mockDescriptor);
    }

    private static class TestMocker extends BaseMocker {
        private TestMocker(WireMockServer wireMockServer, ObjectMapper objectMapper) {
            super(wireMockServer, objectMapper);
        }

        void method() {}
    }
}