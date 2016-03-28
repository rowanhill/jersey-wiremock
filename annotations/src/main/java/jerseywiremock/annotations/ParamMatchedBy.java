package jerseywiremock.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ParamMatchedBy {
    ParamMatchingStrategy value();
}
