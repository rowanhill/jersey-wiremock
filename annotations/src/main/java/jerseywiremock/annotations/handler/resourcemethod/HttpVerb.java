package jerseywiremock.annotations.handler.resourcemethod;

import java.lang.annotation.Annotation;

public enum HttpVerb {
    GET(javax.ws.rs.GET.class),
    POST(javax.ws.rs.POST.class);

    private final Class<? extends Annotation> annotation;

    HttpVerb(Class<? extends Annotation> annotation) {
        this.annotation = annotation;
    }

    Class<? extends Annotation> getAnnotation() {
        return annotation;
    }
}
