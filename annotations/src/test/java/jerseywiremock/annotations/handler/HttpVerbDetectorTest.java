package jerseywiremock.annotations.handler;

import jerseywiremock.annotations.handler.resourcemethod.HttpVerb;
import jerseywiremock.annotations.handler.resourcemethod.HttpVerbDetector;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.ws.rs.GET;

import static org.assertj.core.api.Assertions.*;

public class HttpVerbDetectorTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private HttpVerbDetector verbDetector = new HttpVerbDetector();

    @Test
    public void jerseyGetAnnotationImpliesGetVerb() {
        // when
        HttpVerb verb = verbDetector.getVerbFromAnnotation(TestResource.class, "getMethod");

        // then
        assertThat(verb).isEqualTo(HttpVerb.GET);
    }

    @Test
    public void exceptionIsThrownIfNoKnownAnnotationIsUsed() {
        // when
        expectedException.expectMessage("Could not determine HTTP verb for badMethod");
        verbDetector.getVerbFromAnnotation(TestResource.class, "badMethod");
    }

    private static class TestResource {
        @GET
        void getMethod() {}

        void badMethod() {}
    }
}