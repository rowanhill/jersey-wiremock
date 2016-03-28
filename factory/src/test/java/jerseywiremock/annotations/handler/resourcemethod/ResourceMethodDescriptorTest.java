package jerseywiremock.annotations.handler.resourcemethod;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.assertj.core.api.Assertions.assertThat;

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

    @Test
    public void responseBuilderUsesDefaultStatusCode() {
        // given
        ResourceMethodDescriptor descriptor = new ResourceMethodDescriptor(Object.class, "toString", HttpVerb.GET);

        // when
        ResponseDefinitionBuilder builder = descriptor.toResponseDefinitionBuilder();

        // then
        assertThat(builder.build().getStatus()).isEqualTo(200);
    }

    @Test
    public void responseBuilderUses201StatusCodeForPosts() {
        // given
        ResourceMethodDescriptor descriptor = new ResourceMethodDescriptor(Object.class, "toString", HttpVerb.POST);

        // when
        ResponseDefinitionBuilder builder = descriptor.toResponseDefinitionBuilder();

        // then
        assertThat(builder.build().getStatus()).isEqualTo(201);
    }

    @Test
    public void responseBuilderUses204StatusIfResourceMethodReturnsVoid() {
        // given
        ResourceMethodDescriptor descriptor = new ResourceMethodDescriptor(Object.class, "finalize", HttpVerb.POST);

        // when
        ResponseDefinitionBuilder builder = descriptor.toResponseDefinitionBuilder();

        // then
        assertThat(builder.build().getStatus()).isEqualTo(204);
    }

    @Test
    public void responseBuilderAddsJsonContentTypeByDefault() {
        // given
        ResourceMethodDescriptor descriptor = new ResourceMethodDescriptor(Object.class, "toString", HttpVerb.GET);

        // when
        ResponseDefinitionBuilder builder = descriptor.toResponseDefinitionBuilder();

        // then
        assertThat(builder.build().getHeaders().getHeader("Content-Type").values()).containsOnly("application/json");
    }

    @Test
    public void responseBuilderDoesNotSetContentTypeIfResourceMethodReturnsVoid() {
        // given
        ResourceMethodDescriptor descriptor = new ResourceMethodDescriptor(Object.class, "finalize", HttpVerb.GET);

        // when
        ResponseDefinitionBuilder builder = descriptor.toResponseDefinitionBuilder();

        // then
        assertThat(builder.build().getHeaders()).isNull();
    }
}