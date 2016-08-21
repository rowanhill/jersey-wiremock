package io.jerseywiremock.joda.formatter;

import io.jerseywiremock.annotations.formatter.ParamFormatter;
import org.joda.time.LocalDate;
import org.joda.time.format.ISODateTimeFormat;

public class LocalDateIsoFormatter implements ParamFormatter<LocalDate> {
    @Override
    public String format(LocalDate param) {
        return param == null ? "null" : param.toString(ISODateTimeFormat.date());
    }
}
