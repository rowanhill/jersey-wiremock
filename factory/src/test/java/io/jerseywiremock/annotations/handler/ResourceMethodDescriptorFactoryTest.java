package io.jerseywiremock.annotations.handler;

import io.jerseywiremock.annotations.WireMockForResource;
import io.jerseywiremock.annotations.WireMockStub;
import io.jerseywiremock.annotations.WireMockVerify;
import io.jerseywiremock.annotations.handler.resourcemethod.HttpVerbDetector;
import io.jerseywiremock.annotations.handler.resourcemethod.ResourceMethodDescriptor;
import io.jerseywiremock.annotations.handler.resourcemethod.ResourceMethodDescriptorFactory;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ResourceMethodDescriptorFactoryTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

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
    public void exceptionIsThrownIfMockerTypeIsMissingWireMockForResourceAnnotation() throws Exception {
        // when
        expectedException.expectMessage(
                "Expected TestUnannotatedMocker to be annotated with @WireMockForResource, but it was not");
        methodDescriptorFactory.constructMethodDescriptor(
                TestUnannotatedMocker.class.getMethod("stub"),
                WireMockStub.class);
    }

    @Test
    public void exceptionIsThrownIfMockerMethodIsMissingExpectedWireMockStubAnnotation() throws Exception {
        // when
        expectedException.expectMessage("Expected verify to be annotated with @WireMockStub, but it was not");
        methodDescriptorFactory.constructMethodDescriptor(
                TestMocker.class.getMethod("verify"),
                WireMockStub.class);
    }

    @Test
    public void exceptionIsThrownIfMockerMethodIsMissingExpectedWireMockVerifyAnnotation() throws Exception {
        // when
        expectedException.expectMessage("Expected stub to be annotated with @WireMockVerify, but it was not");
        methodDescriptorFactory.constructMethodDescriptor(
                TestMocker.class.getMethod("stub"),
                WireMockVerify.class);
    }

    @Test
    public void exceptionIsThrownIfUnexpectedMethodAnnotationIsGiven() throws Exception {
        expectedException.expectMessage("Unexpected annotation: WireMockForResource");
        methodDescriptorFactory.constructMethodDescriptor(
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
    private interface TestUnannotatedMocker {
        @WireMockStub("resourceMethod")
        void stub();
    }

    @SuppressWarnings("unused")
    private static class TestResource {
        void resourceMethod() {}
    }
}