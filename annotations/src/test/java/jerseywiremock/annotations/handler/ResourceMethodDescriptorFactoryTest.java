package jerseywiremock.annotations.handler;

import jerseywiremock.annotations.WireMockForResource;
import jerseywiremock.annotations.WireMockStub;
import jerseywiremock.annotations.WireMockVerify;
import jerseywiremock.annotations.factory.ParameterDescriptorsFactory;
import jerseywiremock.core.ParameterDescriptors;
import jerseywiremock.core.RequestMappingDescriptor;
import jerseywiremock.core.RequestMappingDescriptorFactory;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.annotation.Annotation;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ResourceMethodDescriptorFactoryTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private ParameterDescriptorsFactory mockParameterDescriptorsFactory;
    @Mock
    private RequestMappingDescriptorFactory mockRequestMappingDescriptorFactory;
    @InjectMocks
    private ResourceMethodDescriptorFactory methodDescriptorFactory;

    @Test
    public void resourceClassIsTakenFromWireMockForResourceAnnotation() throws Exception {
        // when
        ResourceMethodDescriptor descriptor = methodDescriptorFactory.constructMethodDescriptor(
                new Object[]{},
                TestMocker.class.getMethod("stub"),
                WireMockStub.class);

        // then
        assertThat(descriptor.getResourceClass()).isEqualTo(TestResource.class);
    }

    @Test
    public void methodNameIsTakenFromWireMockStubAnnotation() throws Exception {
        // when
        ResourceMethodDescriptor descriptor = methodDescriptorFactory.constructMethodDescriptor(
                new Object[]{},
                TestMocker.class.getMethod("stub"),
                WireMockStub.class);

        // then
        assertThat(descriptor.getMethodName()).isEqualTo("resourceMethod");
    }

    @Test
    public void methodNameIsTakenFromWireMockVerifyAnnotation() throws Exception {
        // when
        ResourceMethodDescriptor descriptor = methodDescriptorFactory.constructMethodDescriptor(
                new Object[]{},
                TestMocker.class.getMethod("verify"),
                WireMockVerify.class);

        // then
        assertThat(descriptor.getMethodName()).isEqualTo("resourceMethod");
    }

    @Test
    public void mappingDescriptorIsDerivedFromInjectedFactory() throws Exception {
        // given
        ParameterDescriptors mockParameterDescriptors = mock(ParameterDescriptors.class);
        when(mockParameterDescriptorsFactory
                .createParameterDescriptors(new Object[]{}, new Annotation[][]{}, TestResource.class, "resourceMethod"))
                .thenReturn(mockParameterDescriptors);
        RequestMappingDescriptor mockMappingDescriptor = mock(RequestMappingDescriptor.class);
        when(mockRequestMappingDescriptorFactory
                .createMappingDescriptor(TestResource.class, "resourceMethod", mockParameterDescriptors))
                .thenReturn(mockMappingDescriptor);

        // when
        ResourceMethodDescriptor descriptor = methodDescriptorFactory.constructMethodDescriptor(
                new Object[]{},
                TestMocker.class.getMethod("stub"),
                WireMockStub.class);

        // then
        assertThat(descriptor.getRequestMappingDescriptor()).isEqualTo(mockMappingDescriptor);
    }

    @Test
    public void exceptionIsThrownIfMockerMethodIsMissingExpectedWireMockStubAnnotation() throws Exception {
        // when
        expectedException.expectMessage("Expected verify to be annotated with @WireMockStub, but it was not");
        methodDescriptorFactory.constructMethodDescriptor(
                new Object[]{},
                TestMocker.class.getMethod("verify"),
                WireMockStub.class);
    }

    @Test
    public void exceptionIsThrownIfMockerMethodIsMissingExpectedWireMockVerifyAnnotation() throws Exception {
        // when
        expectedException.expectMessage("Expected stub to be annotated with @WireMockVerify, but it was not");
        methodDescriptorFactory.constructMethodDescriptor(
                new Object[]{},
                TestMocker.class.getMethod("stub"),
                WireMockVerify.class);
    }

    @Test
    public void exceptionIsThrownIfUnexpectedMethodAnnotationIsGiven() throws Exception {
        expectedException.expectMessage("Unexpected annotation: WireMockForResource");
        methodDescriptorFactory.constructMethodDescriptor(
                new Object[]{},
                TestMocker.class.getMethod("stub"),
                WireMockForResource.class);
    }

    @SuppressWarnings("unused")
    @WireMockForResource(TestResource.class)
    private interface TestMocker {
        @WireMockStub("resourceMethod")
        void stub();

        @WireMockVerify("resourceMethod")
        void verify();
    }

    @SuppressWarnings("unused")
    private static class TestResource {
        void resourceMethod() {}
    }
}