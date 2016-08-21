package io.jerseywiremock.annotations.handler.requestmatching.paramdescriptors;

import io.jerseywiremock.annotations.formatter.ParamFormatter;

import java.util.Objects;

public class ParamFormatterInvoker {
    public String getFormattedParamValue(Object rawParamValue, Class<? extends ParamFormatter> formatterClass) {
        String formattedValue;
        if (formatterClass != null) {
            ParamFormatter formatter;
            try {
                formatter = formatterClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException("Could not instantiate formatter " + formatterClass.getSimpleName(), e);
            }

            // ParamFormatter is generic, and the generic type is erased by run-time, so we can't check whether
            // rawParamValue is of the right type or not...
            //noinspection unchecked
            formattedValue = formatter.format(rawParamValue);
        } else {
            formattedValue = Objects.toString(rawParamValue);
        }
        return formattedValue;
    }
}
