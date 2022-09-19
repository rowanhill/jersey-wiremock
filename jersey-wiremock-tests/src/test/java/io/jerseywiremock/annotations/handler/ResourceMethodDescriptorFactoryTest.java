package io.jerseywiremock.annotations.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.jerseywiremock.annotations.WireMockForResource;
import io.jerseywiremock.annotations.WireMockStub;
import io.jerseywiremock.annotations.WireMockVerify;
import io.jerseywiremock.annotations.handler.resourcemethod.HttpVerbDetector;
import io.jerseywiremock.annotations.handler.resourcemethod.ResourceMethodDescriptor;
import io.jerseywiremock.annotations.handler.resourcemethod.ResourceMethodDescriptorFactory;

@ExtendWith({MockitoExtension.class})
public class ResourceMethodDescriptorFactoryTest {
    @Mock
    private HttpVerbDetector mockHttpVerbDetector;
    @InjectMocks
    private ResourceMethodDescriptorFactory methodDescriptorFactory;

    @Test
    public void resourceClassIsTakenFromWireMockForResourceAnnotation() throws Exception {
        // when
        ResourceMethodDescriptor descriptor = methodDescriptorFactory.constructMethodDescriptor(
                TestMocker.class.getMethod("stub"),
                WireMockStub.class);

        // then
        assertThat(descriptor.getResourceClass()).isEqualTo(TestResource.class);
    }

    @Test
    public void methodNameIsTakenFromWireMockStubAnnotation() throws Exception {
        // when
        ResourceMethodDescriptor descriptor = methodDescriptorFactory.constructMethodDescriptor(
                TestMocker.class.getMethod("stub"),
                WireMockStub.class);

        // then
        assertThat(descriptor.getMethodName()).isEqualTo("resourceMethod");
    }

    @Test
    public void methodNameIsTakenFromWireMockVerifyAnnotation() throws Exception {
        // when
        ResourceMethodDescriptor descriptor = methodDescriptorFactory.constructMethodDescriptor(
                TestMocker.class.getMethod("verify"),
                WireMockVerify.class);

        // then
        assertThat(descriptor.getMethodName()).isEqualTo("resourceMethod");
    }

    @Test
    public void exceptionIsThrownIfMockerTypeIsMissingWireMockForResourceAnnotation() {
        // when
        assertThrows(Exception.class, () -> methodDescriptorFactory.constructMethodDescriptor(
                TestUnannotatedMocker.class.getMethod("stub"),
                WireMockStub.class
        ));
    }

    @Test
    public void exceptionIsThrownIfMockerMethodIsMissingExpectedWireMockStubAnnotation() throws Exception {
        // when
        assertThrows(Exception.class, () -> methodDescriptorFactory.constructMethodDescriptor(
                TestMocker.class.getMethod("verify"),
                WireMockStub.class
        ));
    }

    @Test
    public void exceptionIsThrownIfMockerMethodIsMissingExpectedWireMockVerifyAnnotation() throws Exception {
        // when
        assertThrows(Exception.class, () -> methodDescriptorFactory.constructMethodDescriptor(
                TestMocker.class.getMethod("stub"),
                WireMockVerify.class
        ));
    }

    @Test
    public void exceptionIsThrownIfUnexpectedMethodAnnotationIsGiven() throws Exception {
        assertThrows(Exception.class, () -> methodDescriptorFactory.constructMethodDescriptor(
                TestMocker.class.getMethod("stub"),
                WireMockForResource.class
        ));
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
    private interface TestUnannotatedMocker {
        @WireMockStub("resourceMethod")
        void stub();
    }

    @SuppressWarnings("unused")
    private static class TestResource {
        void resourceMethod() {}
    }
}