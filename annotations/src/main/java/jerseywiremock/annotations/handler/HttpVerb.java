package jerseywiremock.annotations.handler;

import java.lang.annotation.Annotation;

enum HttpVerb {
    GET(javax.ws.rs.GET.class),
    POST(javax.ws.rs.POST.class);

    private final Class<? extends Annotation> annotation;

    HttpVerb(Class<? extends Annotation> annotation) {
        this.annotation = annotation;
    }

    public Class<? extends Annotation> getAnnotation() {
        return annotation;
    }
}
