package jerseywiremock.annotations.handler.resourcemethod;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ResourceMethodDescriptorTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void assertingVerbThrowsExceptionForDifferentVerb() {
        // given
        ResourceMethodDescriptor descriptor = new ResourceMethodDescriptor(Object.class, "method", HttpVerb.GET);

        // when
        expectedException.expectMessage("Expected method to be annotated with @POST");
        descriptor.assertVerb(HttpVerb.POST);
    }

    @Test
    public void assertingVerbDoesNothingForSameVerb() {
        // given
        ResourceMethodDescriptor descriptor = new ResourceMethodDescriptor(Object.class, "method", HttpVerb.GET);

        // when
        descriptor.assertVerb(HttpVerb.GET);
    }
}