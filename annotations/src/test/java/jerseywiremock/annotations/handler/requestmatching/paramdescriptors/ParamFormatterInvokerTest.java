package jerseywiremock.annotations.handler.requestmatching.paramdescriptors;

import jerseywiremock.formatter.ParamFormatter;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Date;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.CoreMatchers.isA;

public class ParamFormatterInvokerTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private ParamFormatterInvoker invoker = new ParamFormatterInvoker();

    @Test
    public void invokerDelegatesToSpecifiedClass() {
        // when
        String formattedParamValue = invoker.getFormattedParamValue(new Date(), StaticFormatter.class);

        // then
        assertThat(formattedParamValue).isEqualTo("formatted");
    }

    @Test
    public void invokerUsesToStringIfNoFormatterSpecified() {
        // given
        Date date = new Date();

        // when
        String formattedParamValue = invoker.getFormattedParamValue(date, null);

        // then
        assertThat(formattedParamValue).isEqualTo(date.toString());
    }

    @Test
    public void invokerReturnsStringContainingWordNullIfNoFormatterSpecifiedAndValueIsNull() {
        // when
        String formattedParamValue = invoker.getFormattedParamValue(null, null);

        // then
        assertThat(formattedParamValue).isEqualTo("null");
    }

    @Test
    public void usingAbstractFormatterClassThrowsException() {
        // when
        expectedException.expectMessage("Could not instantiate formatter AbstractFormatter");
        expectedException.expectCause(isA(InstantiationException.class));
        invoker.getFormattedParamValue(null, AbstractFormatter.class);
    }

    @Test
    public void usingNoNullConstructorFormatterClassThrowsException() {
        // when
        expectedException.expectMessage("Could not instantiate formatter NoNullConstructorFormatter");
        expectedException.expectCause(isA(InstantiationException.class));
        invoker.getFormattedParamValue(null, NoNullConstructorFormatter.class);
    }

    @Test
    public void usingPrivateFormatterClassThrowsException() {
        // when
        expectedException.expectMessage("Could not instantiate formatter PrivateFormatter");
        expectedException.expectCause(isA(IllegalAccessException.class));
        invoker.getFormattedParamValue(null, PrivateFormatter.class);
    }

    static class StaticFormatter implements ParamFormatter<Date> {
        @Override
        public String format(Date param) {
            return "formatted";
        }
    }

    static abstract class AbstractFormatter implements ParamFormatter<Date> {

    }

    static class NoNullConstructorFormatter implements ParamFormatter<Date> {
        private final String result;

        NoNullConstructorFormatter(String result) {
            this.result = result;
        }

        @Override
        public String format(Date param) {
            return result;
        }
    }

    private static class PrivateFormatter implements ParamFormatter<Date> {
        @Override
        public String format(Date param) {
            return "This formatter should be inaccessible, because it is private";
        }
    }
}