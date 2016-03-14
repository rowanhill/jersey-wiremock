package jerseywiremock.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.CLASS)
public @interface JerseyWireMock {
    public Class<?> value();
}
