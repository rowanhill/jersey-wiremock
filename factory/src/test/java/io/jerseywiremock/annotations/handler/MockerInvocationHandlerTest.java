package io.jerseywiremock.annotations.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.jerseywiremock.annotations.WireMockStub;
import io.jerseywiremock.annotations.WireMockVerify;
import io.jerseywiremock.annotations.handler.requestmatching.RequestMatchingDescriptor;
import io.jerseywiremock.annotations.handler.requestmatching.RequestMatchingDescriptorFactory;
import io.jerseywiremock.annotations.handler.resourcemethod.HttpVerb;
import io.jerseywiremock.annotations.handler.resourcemethod.ResourceMethodDescriptor;
import io.jerseywiremock.annotations.handler.resourcemethod.ResourceMethodDescriptorFactory;
import io.jerseywiremock.annotations.handler.util.CollectionFactory;
import io.jerseywiremock.core.stub.request.*;
import io.jerseywiremock.core.verify.DeleteRequestVerifier;
import io.jerseywiremock.core.verify.GetRequestVerifier;
import io.jerseywiremock.core.verify.PostRequestVerifier;
import io.jerseywiremock.core.verify.PutRequestVerifier;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.core.UriBuilder;
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
    private RequestMatchingDescriptorFactory mockRequestMatchingDescriptorFactory;
    @Mock
    private CollectionFactory mockCollectionFactory;
    @InjectMocks
    private MockerInvocationHandler handler;

    private Object[] params;
    private TestMocker testMocker;
    private Method mockerMethod;
    private ResourceMethodDescriptor mockDescriptor;
    private Method resourceMethod;
    private UriBuilder uriBuilder;

    @Before
    public void setUp() throws Exception {
        params = new Object[]{};
        testMocker = new TestMocker(null, null);
        mockerMethod = TestMocker.class.getDeclaredMethod("method");
        uriBuilder = UriBuilder.fromPath("/test");
        resourceMethod = TestResource.class.getDeclaredMethod("method");

        mockDescriptor = mock(ResourceMethodDescriptor.class);
        when(mockDescriptor.getMethodName()).thenReturn("method");
        when(mockDescriptor.createUriBuilder()).thenReturn(uriBuilder);
        when(mockDescriptor.getMethod()).thenReturn(resourceMethod);

        RequestMatchingDescriptor mockRequestMatchingDescriptor = mock(RequestMatchingDescriptor.class);
        when(mockRequestMatchingDescriptorFactory.createRequestMatchingDescriptor(
                eq(resourceMethod),
                eq(mockerMethod),
                eq(params),
                eq(uriBuilder),
                any(RequestMatchingDescriptorFactory.QueryParamEncodingStrategy.class))
        ).thenReturn(mockRequestMatchingDescriptor);
    }

    @Test
    public void handlingStubGetCreatesGetStubber() {
        // given
        stubResourceMethodDescriptorFor(WireMockStub.class);

        // when
        GetSingleRequestStubber<Object> getRequestStubber = handler.handleStubGet(params, testMocker, mockerMethod);

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
        handler.handleStubGet(params, testMocker, mockerMethod);
    }

    @Test
    public void handlingStubListCreatesListStubber() {
        // given
        stubResourceMethodDescriptorFor(WireMockStub.class);

        // when
        GetListRequestStubber<Object> listRequestStubber = handler.handleStubList(params, testMocker, mockerMethod);

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
        handler.handleStubList(params, testMocker, mockerMethod);
    }

    @Test
    public void handlingVerifyGetVerbCreatesGetVerifier() {
        // given
        stubResourceMethodDescriptorFor(WireMockVerify.class);

        // when
        GetRequestVerifier getRequestVerifier = handler.handleVerifyGetVerb(params, testMocker, mockerMethod);

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
        handler.handleVerifyGetVerb(params, testMocker, mockerMethod);
    }

    @Test
    public void handlingStubPostCreatesPostRequestStubber() {
        // given
        stubResourceMethodDescriptorFor(WireMockStub.class);

        // when
        PostRequestStubber<Object, Object> stubber =
                handler.handleStubPost(params, testMocker, mockerMethod);

        // then
        assertThat(stubber).isNotNull();
    }

    @Test
    public void handlingVerifyPostCreatesPostRequestVerifier() {
        // given
        stubResourceMethodDescriptorFor(WireMockVerify.class);

        // when
        PostRequestVerifier<Object> verifier =
                handler.handleVerifyPostVerb(params, testMocker, mockerMethod);

        // then
        assertThat(verifier).isNotNull();
    }

    @Test
    public void handlingStubPutCreatesPutRequestStubber() {
        // given
        stubResourceMethodDescriptorFor(WireMockStub.class);

        // when
        PutRequestStubber<Object, Object> stubber =
                handler.handleStubPut(params, testMocker, mockerMethod);

        // then
        assertThat(stubber).isNotNull();
    }

    @Test
    public void handlingVerifyPutCreatesPutRequestVerifier() {
        // given
        stubResourceMethodDescriptorFor(WireMockVerify.class);

        // when
        PutRequestVerifier<Object> verifier =
                handler.handleVerifyPutVerb(params, testMocker, mockerMethod);

        // then
        assertThat(verifier).isNotNull();
    }

    @Test
    public void handlingStubDeleteCreatesDeleteRequestStubber() {
        // given
        stubResourceMethodDescriptorFor(WireMockStub.class);

        // when
        DeleteRequestStubber<Object> stubber =
                handler.handleStubDelete(params, testMocker, mockerMethod);

        // then
        assertThat(stubber).isNotNull();
    }

    @Test
    public void handlingVerifyDeleteCreatesDeleteRequestVerifier() {
        // given
        stubResourceMethodDescriptorFor(WireMockVerify.class);

        // when
        DeleteRequestVerifier verifier =
                handler.handleVerifyDeleteVerb(params, testMocker, mockerMethod);

        // then
        assertThat(verifier).isNotNull();
    }

    @Test
    public void handlingStubbingEncodesQueryParams() {
        // given
        stubResourceMethodDescriptorFor(WireMockStub.class);

        // when
        GetSingleRequestStubber<Object> stubber = handler.handleStubGet(params, testMocker, mockerMethod);

        // then
        assertThat(stubber).isNotNull();
        verify(mockRequestMatchingDescriptorFactory).createRequestMatchingDescriptor(
                resourceMethod,
                mockerMethod,
                params,
                uriBuilder,
                RequestMatchingDescriptorFactory.QueryParamEncodingStrategy.ENCODED
        );
    }

    @Test
    public void handlingVerifyingEncodesQueryParams() {
        // given
        stubResourceMethodDescriptorFor(WireMockVerify.class);

        // when
        GetRequestVerifier stubber = handler.handleVerifyGetVerb(params, testMocker, mockerMethod);

        // then
        assertThat(stubber).isNotNull();
        verify(mockRequestMatchingDescriptorFactory).createRequestMatchingDescriptor(
                resourceMethod,
                mockerMethod,
                params,
                uriBuilder,
                RequestMatchingDescriptorFactory.QueryParamEncodingStrategy.UNENCODED
        );
    }

    private void stubResourceMethodDescriptorFor(Class<? extends Annotation> methodAnnotation) {
        when(mockResourceMethodDescriptorFactory.constructMethodDescriptor(mockerMethod, methodAnnotation))
                .thenReturn(mockDescriptor);
    }

    @SuppressWarnings("unused")
    private static class TestMocker extends BaseMocker {
        private TestMocker(WireMockServer wireMockServer, ObjectMapper objectMapper) {
            super(wireMockServer, objectMapper);
        }

        void method() {}
    }

    @SuppressWarnings("unused")
    private static class TestResource {
        void method() {}
    }
}