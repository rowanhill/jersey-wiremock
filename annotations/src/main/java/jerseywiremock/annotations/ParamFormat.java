package jerseywiremock.annotations;

import jerseywiremock.formatter.ParamFormatter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ParamFormat {
    Class<? extends ParamFormatter> value();
}
