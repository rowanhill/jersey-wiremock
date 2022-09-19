package io.jerseywiremock.annotations.handler.requestmatching.paramdescriptors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Date;

import org.junit.jupiter.api.Test;

import io.jerseywiremock.annotations.formatter.ParamFormatter;

public class ParamFormatterInvokerTest {
    private final ParamFormatterInvoker invoker = new ParamFormatterInvoker();

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
        assertThrows(RuntimeException.class, () -> invoker.getFormattedParamValue(null, AbstractFormatter.class));
    }

    @Test
    public void usingNoNullConstructorFormatterClassThrowsException() {
        assertThrows(RuntimeException.class, () -> invoker.getFormattedParamValue(null, NoNullConstructorFormatter.class));
    }

    @Test
    public void usingPrivateFormatterClassThrowsException() {
        assertThrows(Exception.class, () -> invoker.getFormattedParamValue(null, PrivateFormatter.class));
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