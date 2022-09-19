package io.jerseywiremock.annotations.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.ws.rs.GET;

import org.junit.jupiter.api.Test;

import io.jerseywiremock.annotations.handler.resourcemethod.HttpVerb;
import io.jerseywiremock.annotations.handler.resourcemethod.HttpVerbDetector;

public class HttpVerbDetectorTest {
    private final HttpVerbDetector verbDetector = new HttpVerbDetector();

    @Test
    public void jerseyGetAnnotationImpliesGetVerb() {
        // when
        HttpVerb verb = verbDetector.getVerbFromAnnotation(TestResource.class, "getMethod");

        // then
        assertThat(verb).isEqualTo(HttpVerb.GET);
    }

    @Test
    public void exceptionIsThrownIfNoKnownAnnotationIsUsed() {
        assertThrows(Exception.class, () -> verbDetector.getVerbFromAnnotation(TestResource.class, "badMethod"));
    }

    private static class TestResource {
        @GET
        public void getMethod() {}

        void badMethod() {}
    }
}